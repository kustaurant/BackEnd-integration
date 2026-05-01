package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.RestaurantRawSaveService;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStartResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateEntity;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateRepository;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
// 폐점 후보 자동 판별/자동 처리 및 진행률 job 상태 관리를 담당하는 서비스.
public class ClosedCandidateAutoProcessService {

    private static final String NAVER_PLACE_NOT_FOUND_TEXT = "요청하신 페이지를 찾을 수 없습니다.";

    private final RestaurantSyncCandidateRepository candidateRepository;
    private final RestaurantCrawlerClient restaurantCrawlerClient;
    private final RestaurantRawSaveService restaurantRawSaveService;
    private final RestaurantSyncApplyService applyService;

    private final Map<String, ClosedAutoProcessJobState> jobs = new ConcurrentHashMap<>();

    @Transactional
    public ClosedCandidateAutoProcessResponse autoProcessClosedCandidates(String reviewedBy) {
        List<RestaurantSyncCandidateEntity> closedCandidates = loadPendingClosedCandidates();
        int autoClosedCount = 0;
        int recrawledCount = 0;
        int failedCount = 0;

        for (RestaurantSyncCandidateEntity candidate : closedCandidates) {
            String originalPlaceId = candidate.getPlaceId();
            String lookupPlaceId = toLookupPlaceId(originalPlaceId);
            try {
                RestaurantRaw analyzed = restaurantCrawlerClient.analyzeOne(lookupPlaceId);
                if (isClosedByNotFoundMessage(analyzed)) {
                    applyService.applyClosedRestaurant(originalPlaceId);
                    candidateRepository.delete(candidate);
                    autoClosedCount++;
                    log.info("폐점 후보 자동처리: 폐점 처리 완료. candidateId={}, placeId={}, lookupPlaceId={}",
                            candidate.getId(), originalPlaceId, lookupPlaceId);
                    continue;
                }

                ZoneType zoneType = analyzed.crawlScope() == null ? ZoneType.OUT_OF_ZONE : analyzed.crawlScope();
                restaurantRawSaveService.saveResult(analyzed, zoneType);
                candidateRepository.delete(candidate);
                recrawledCount++;
                log.info("폐점 후보 자동처리: 재크롤 raw 저장 완료. candidateId={}, placeId={}, lookupPlaceId={}, zoneType={}",
                        candidate.getId(), originalPlaceId, lookupPlaceId, zoneType);
            } catch (Exception e) {
                failedCount++;
                log.info("폐점 후보 자동처리 실패. candidateId={}, placeId={}, lookupPlaceId={}, reason={}",
                        candidate.getId(), originalPlaceId, lookupPlaceId, e.getMessage());
            }
        }

        log.info("폐점 후보 자동처리 집계. totalPendingClosed={}, autoClosedCount={}, recrawledCount={}, failedCount={}",
                closedCandidates.size(), autoClosedCount, recrawledCount, failedCount);
        return new ClosedCandidateAutoProcessResponse(closedCandidates.size(), autoClosedCount, recrawledCount, failedCount);
    }

    public ClosedCandidateAutoProcessJobStartResponse startClosedAutoProcessJob(String reviewedBy) {
        String jobId = UUID.randomUUID().toString();
        ClosedAutoProcessJobState state = new ClosedAutoProcessJobState(jobId);
        jobs.put(jobId, state);
        CompletableFuture.runAsync(() -> runJob(state, reviewedBy));
        return new ClosedCandidateAutoProcessJobStartResponse(jobId);
    }

    public ClosedCandidateAutoProcessJobStatusResponse getClosedAutoProcessJobStatus(String jobId) {
        ClosedAutoProcessJobState state = jobs.get(jobId);
        if (state == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "closed auto process job not found: " + jobId);
        }
        return state.toResponse();
    }

    private void runJob(ClosedAutoProcessJobState state, String reviewedBy) {
        state.markRunning();
        try {
            List<RestaurantSyncCandidateEntity> candidates = loadPendingClosedCandidates();
            state.setTotal(candidates.size());
            for (RestaurantSyncCandidateEntity candidate : candidates) {
                String originalPlaceId = candidate.getPlaceId();
                String lookupPlaceId = toLookupPlaceId(originalPlaceId);
                try {
                    RestaurantRaw analyzed = restaurantCrawlerClient.analyzeOne(lookupPlaceId);
                    if (isClosedByNotFoundMessage(analyzed)) {
                        applyService.applyClosedRestaurant(originalPlaceId);
                        candidateRepository.delete(candidate);
                        state.incAutoClosed();
                        log.info("폐점 후보 자동처리: 폐점 처리 완료. candidateId={}, placeId={}, lookupPlaceId={}",
                                candidate.getId(), originalPlaceId, lookupPlaceId);
                    } else {
                        ZoneType zoneType = analyzed.crawlScope() == null ? ZoneType.OUT_OF_ZONE : analyzed.crawlScope();
                        restaurantRawSaveService.saveResult(analyzed, zoneType);
                        candidateRepository.delete(candidate);
                        state.incRecrawled();
                        log.info("폐점 후보 자동처리: 재크롤 raw 저장 완료. candidateId={}, placeId={}, lookupPlaceId={}, zoneType={}",
                                candidate.getId(), originalPlaceId, lookupPlaceId, zoneType);
                    }
                } catch (Exception e) {
                    state.incFailed();
                    log.info("폐점 후보 자동처리 실패. candidateId={}, placeId={}, lookupPlaceId={}, reason={}",
                            candidate.getId(), originalPlaceId, lookupPlaceId, e.getMessage());
                } finally {
                    state.incProcessed();
                }
            }
            log.info("폐점 후보 자동처리 집계. totalPendingClosed={}, autoClosedCount={}, recrawledCount={}, failedCount={}",
                    state.total, state.autoClosedCount, state.recrawledCount, state.failedCount);
            state.markCompleted();
        } catch (Exception e) {
            state.markFailed();
            log.info("폐점 후보 자동처리 job 실패. jobId={}, reason={}", state.jobId, e.getMessage());
        }
    }

    private List<RestaurantSyncCandidateEntity> loadPendingClosedCandidates() {
        return candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus.PENDING)
                .stream()
                .filter(candidate -> candidate.getCandidateType() == SyncCandidateType.CLOSED)
                .toList();
    }

    private boolean isClosedByNotFoundMessage(RestaurantRaw analyzed) {
        if (analyzed == null) return false;
        String address = analyzed.restaurantAddress();
        return address != null && address.contains(NAVER_PLACE_NOT_FOUND_TEXT);
    }

    private String toLookupPlaceId(String placeId) {
        if (placeId == null) return "";
        int idx = placeId.indexOf('_');
        if (idx <= 0) return placeId;
        return placeId.substring(0, idx);
    }

    private static final class ClosedAutoProcessJobState {
        private final String jobId;
        private String status = "PENDING";
        private int total;
        private int processed;
        private int autoClosedCount;
        private int recrawledCount;
        private int failedCount;
        private boolean done;

        private ClosedAutoProcessJobState(String jobId) {
            this.jobId = jobId;
        }

        private synchronized void markRunning() { this.status = "RUNNING"; }
        private synchronized void markCompleted() { this.status = "COMPLETED"; this.done = true; }
        private synchronized void markFailed() { this.status = "FAILED"; this.done = true; }
        private synchronized void setTotal(int total) { this.total = total; }
        private synchronized void incProcessed() { this.processed++; }
        private synchronized void incAutoClosed() { this.autoClosedCount++; }
        private synchronized void incRecrawled() { this.recrawledCount++; }
        private synchronized void incFailed() { this.failedCount++; }

        private synchronized ClosedCandidateAutoProcessJobStatusResponse toResponse() {
            return new ClosedCandidateAutoProcessJobStatusResponse(
                    jobId, status, total, processed, autoClosedCount, recrawledCount, failedCount, done
            );
        }
    }
}
