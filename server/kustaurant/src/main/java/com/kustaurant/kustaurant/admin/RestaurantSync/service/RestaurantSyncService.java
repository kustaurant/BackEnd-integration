package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStartResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.NewCandidateAutoApproveResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateActionResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncRunResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.map.ZoneType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// RestaurantSync 도메인 유스케이스를 하위 서비스에 위임하는 파사드 서비스.
public class RestaurantSyncService {

    private final RestaurantSyncCandidateService candidateService;
    private final ClosedCandidateAutoProcessService closedCandidateAutoProcessService;

    public RestaurantSyncRunResponse generateCandidatesAndSync(ZoneType crawlScope) {
        return candidateService.generateCandidatesAndSync(crawlScope);
    }

    public List<RestaurantSyncCandidateResponse> getCandidates(SyncCandidateStatus status) {
        return candidateService.getCandidates(status);
    }

    public RestaurantSyncCandidateActionResponse approve(Long candidateId, String reviewedBy, String manualCuisine) {
        return candidateService.approve(candidateId, reviewedBy, manualCuisine);
    }

    public RestaurantSyncCandidateActionResponse reject(Long candidateId, String reviewedBy) {
        return candidateService.reject(candidateId, reviewedBy);
    }

    public ClosedCandidateAutoProcessResponse autoProcessClosedCandidates(String reviewedBy) {
        return closedCandidateAutoProcessService.autoProcessClosedCandidates(reviewedBy);
    }

    public ClosedCandidateAutoProcessJobStartResponse startClosedAutoProcessJob(String reviewedBy) {
        return closedCandidateAutoProcessService.startClosedAutoProcessJob(reviewedBy);
    }

    public ClosedCandidateAutoProcessJobStatusResponse getClosedAutoProcessJobStatus(String jobId) {
        return closedCandidateAutoProcessService.getClosedAutoProcessJobStatus(jobId);
    }

    public NewCandidateAutoApproveResponse autoApproveNewCandidates(String reviewedBy) {
        return candidateService.autoApproveNewCandidates(reviewedBy);
    }
}
