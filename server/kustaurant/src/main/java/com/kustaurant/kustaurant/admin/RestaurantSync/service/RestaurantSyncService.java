package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.service.RestaurantRawSaveService;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStartResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessJobStatusResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.ClosedCandidateAutoProcessResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.NewCandidateAutoApproveResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateActionResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncRunResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncUpdatedItemResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantMenuJpaRepository;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateEntity;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantJpaRepository;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurant.entity.RestaurantEntity;
import com.kustaurant.restaurant.entity.RestaurantMenuEntity;
import com.kustaurant.restaurantSync.RestaurantRaw;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantSyncService {

    private static final String SUCCESS = "SUCCESS";
    private static final String ACTIVE = "ACTIVE";
    private static final String NAVER_PLACE_NOT_FOUND_TEXT = "요청하신 페이지를 찾을 수 없습니다.";
    private static final String UNKNOWN_CATEGORY = "기타";
    private static final List<String> FIXED_CUISINES = List.of(
            "한식", "일식", "중식", "양식", "아시안", "고기", "치킨",
            "해산물", "햄버거/피자", "분식", "술집", "카페/디저트", "베이커리", "샐러드"
    );

    private final RestaurantCrawlRawRepository rawRepository;
    private final RestaurantMenuRawRepository menuRawRepository;
    private final RestaurantJpaRepository restaurantRepository;
    private final RestaurantMenuJpaRepository restaurantMenuRepository;
    private final RestaurantSyncCandidateRepository candidateRepository;
    private final RestaurantRawSaveService restaurantRawSaveService;
    private final RestaurantCrawlerClient restaurantCrawlerClient;
    private final Map<String, ClosedAutoProcessJobState> closedAutoProcessJobs = new ConcurrentHashMap<>();

    @Transactional
    public RestaurantSyncRunResponse generateCandidatesAndSync(ZoneType crawlScope) {
        List<RestaurantCrawlRawEntity> raws = loadRaws(crawlScope);
        Map<Long, List<RestaurantMenuCrawlRawEntity>> rawMenusByRawId = loadRawMenus(raws);
        Map<String, RestaurantCrawlRawEntity> rawByPlaceId = raws.stream()
                .collect(Collectors.toMap(RestaurantCrawlRawEntity::getSourcePlaceId, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        List<RestaurantEntity> restaurants = loadRestaurants(crawlScope);
        Map<String, RestaurantEntity> restaurantByPlaceId = restaurants.stream()
                .collect(Collectors.toMap(RestaurantEntity::getPlaceId, Function.identity(), (a, b) -> a, LinkedHashMap::new));

        List<String> newCandidatePlaceIds = createNewCandidates(rawByPlaceId.keySet(), restaurantByPlaceId.keySet());
        List<String> closedCandidatePlaceIds = createClosedCandidates(rawByPlaceId.keySet(), restaurantByPlaceId.keySet());
        List<String> updatedPlaceIds = updateIntersectedRestaurants(rawByPlaceId, restaurantByPlaceId, rawMenusByRawId);
        List<RestaurantSyncUpdatedItemResponse> updatedRestaurants = updatedPlaceIds.stream()
                .map(placeId -> {
                    RestaurantEntity restaurant = restaurantByPlaceId.get(placeId);
                    if (restaurant == null) {
                        return new RestaurantSyncUpdatedItemResponse(
                                placeId,
                                placeId,
                                "https://map.naver.com/p/entry/place/" + placeId
                        );
                    }
                    return new RestaurantSyncUpdatedItemResponse(
                            placeId,
                            restaurant.getRestaurantName(),
                            "/restaurants/" + restaurant.getRestaurantId()
                    );
                })
                .toList();

        return new RestaurantSyncRunResponse(
                rawByPlaceId.size(),
                restaurantByPlaceId.size(),
                newCandidatePlaceIds.size(),
                closedCandidatePlaceIds.size(),
                updatedPlaceIds.size(),
                List.copyOf(newCandidatePlaceIds),
                List.copyOf(closedCandidatePlaceIds),
                List.copyOf(updatedPlaceIds),
                updatedRestaurants
        );
    }

    @Transactional(readOnly = true)
    public List<RestaurantSyncCandidateResponse> getCandidates(SyncCandidateStatus status) {
        List<RestaurantSyncCandidateEntity> candidates = candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(status);
        Set<String> placeIds = candidates.stream()
                .map(RestaurantSyncCandidateEntity::getPlaceId)
                .collect(Collectors.toSet());
        if (placeIds.isEmpty()) {
            return List.of();
        }

        Map<String, RestaurantEntity> restaurantsByPlaceId = restaurantRepository.findAllByPlaceIdIn(placeIds)
                .stream()
                .collect(Collectors.toMap(RestaurantEntity::getPlaceId, Function.identity(), (a, b) -> a));
        Map<String, RestaurantCrawlRawEntity> rawsByPlaceId = rawRepository.findAllBySourcePlaceIdIn(placeIds)
                .stream()
                .collect(Collectors.toMap(RestaurantCrawlRawEntity::getSourcePlaceId, Function.identity(), (a, b) -> a));

        return candidates.stream()
                .map(candidate -> toResponse(candidate, restaurantsByPlaceId.get(candidate.getPlaceId()), rawsByPlaceId.get(candidate.getPlaceId())))
                .toList();
    }

    @Transactional
    public RestaurantSyncCandidateActionResponse approve(Long candidateId, String reviewedBy, String manualCuisine) {
        RestaurantSyncCandidateEntity candidate = candidateRepository
                .findByIdAndCandidateStatus(candidateId, SyncCandidateStatus.PENDING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "pending candidate not found: " + candidateId));

        if (candidate.getCandidateType() == SyncCandidateType.NEW) {
            applyNewRestaurant(candidate.getPlaceId(), manualCuisine);
        } else {
            applyClosedRestaurant(candidate.getPlaceId());
        }

        candidateRepository.delete(candidate);
        return new RestaurantSyncCandidateActionResponse(candidateId, SyncCandidateStatus.APPROVED.name());
    }

    @Transactional
    public RestaurantSyncCandidateActionResponse reject(Long candidateId, String reviewedBy) {
        RestaurantSyncCandidateEntity candidate = candidateRepository
                .findByIdAndCandidateStatus(candidateId, SyncCandidateStatus.PENDING)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "pending candidate not found: " + candidateId));

        candidateRepository.delete(candidate);
        return new RestaurantSyncCandidateActionResponse(candidateId, SyncCandidateStatus.REJECTED.name());
    }

    @Transactional
    public ClosedCandidateAutoProcessResponse autoProcessClosedCandidates(String reviewedBy) {
        List<RestaurantSyncCandidateEntity> closedCandidates = candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus.PENDING)
                .stream()
                .filter(candidate -> candidate.getCandidateType() == SyncCandidateType.CLOSED)
                .toList();

        int autoClosedCount = 0;
        int recrawledCount = 0;
        int failedCount = 0;

        for (RestaurantSyncCandidateEntity candidate : closedCandidates) {
            String originalPlaceId = candidate.getPlaceId();
            String lookupPlaceId = toLookupPlaceId(originalPlaceId);
            try {
                RestaurantRaw analyzed = restaurantCrawlerClient.analyzeOne(lookupPlaceId);

                if (isClosedByNotFoundMessage(analyzed)) {
                    applyClosedRestaurant(originalPlaceId);
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

        return new ClosedCandidateAutoProcessResponse(
                closedCandidates.size(),
                autoClosedCount,
                recrawledCount,
                failedCount
        );
    }

    public ClosedCandidateAutoProcessJobStartResponse startClosedAutoProcessJob(String reviewedBy) {
        String jobId = UUID.randomUUID().toString();
        ClosedAutoProcessJobState state = new ClosedAutoProcessJobState(jobId);
        closedAutoProcessJobs.put(jobId, state);

        CompletableFuture.runAsync(() -> runClosedAutoProcessJob(state, reviewedBy));
        return new ClosedCandidateAutoProcessJobStartResponse(jobId);
    }

    public ClosedCandidateAutoProcessJobStatusResponse getClosedAutoProcessJobStatus(String jobId) {
        ClosedAutoProcessJobState state = closedAutoProcessJobs.get(jobId);
        if (state == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "closed auto process job not found: " + jobId);
        }
        return state.toResponse();
    }

    private void runClosedAutoProcessJob(ClosedAutoProcessJobState state, String reviewedBy) {
        state.markRunning();
        try {
            List<RestaurantSyncCandidateEntity> closedCandidates =
                    candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus.PENDING)
                            .stream()
                            .filter(candidate -> candidate.getCandidateType() == SyncCandidateType.CLOSED)
                            .toList();
            state.setTotal(closedCandidates.size());

            for (RestaurantSyncCandidateEntity candidate : closedCandidates) {
                String originalPlaceId = candidate.getPlaceId();
                String lookupPlaceId = toLookupPlaceId(originalPlaceId);
                try {
                    RestaurantRaw analyzed = restaurantCrawlerClient.analyzeOne(lookupPlaceId);
                    if (isClosedByNotFoundMessage(analyzed)) {
                        applyClosedRestaurant(originalPlaceId);
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

    @Transactional
    public NewCandidateAutoApproveResponse autoApproveNewCandidates(String reviewedBy) {
        List<RestaurantSyncCandidateEntity> newCandidates = candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus.PENDING)
                .stream()
                .filter(candidate -> candidate.getCandidateType() == SyncCandidateType.NEW)
                .toList();

        int approvedCount = 0;
        int failedCount = 0;

        for (RestaurantSyncCandidateEntity candidate : newCandidates) {
            try {
                applyNewRestaurant(candidate.getPlaceId(), null);
                candidateRepository.delete(candidate);
                approvedCount++;
            } catch (Exception e) {
                failedCount++;
                log.warn("신규 후보 자동 승인 실패. candidateId={}, placeId={}, reason={}",
                        candidate.getId(), candidate.getPlaceId(), e.getMessage());
            }
        }

        return new NewCandidateAutoApproveResponse(
                newCandidates.size(),
                approvedCount,
                failedCount
        );
    }

    private List<RestaurantCrawlRawEntity> loadRaws(ZoneType crawlScope) {
        if (crawlScope == null) {
            return rawRepository.findAllByCrawlStatus(SUCCESS);
        }
        return rawRepository.findAllByCrawlStatusAndCrawlScope(SUCCESS, crawlScope);
    }

    private Map<Long, List<RestaurantMenuCrawlRawEntity>> loadRawMenus(List<RestaurantCrawlRawEntity> raws) {
        if (raws.isEmpty()) {
            return Map.of();
        }
        List<Long> rawIds = raws.stream().map(RestaurantCrawlRawEntity::getId).toList();
        return menuRawRepository.findAllByRestaurantRawIdIn(rawIds).stream()
                .collect(Collectors.groupingBy(RestaurantMenuCrawlRawEntity::getRestaurantRawId));
    }

    private List<RestaurantEntity> loadRestaurants(ZoneType crawlScope) {
        if (crawlScope == null) {
            return restaurantRepository.findAllByStatus(ACTIVE);
        }
        return restaurantRepository.findAllByStatusAndRestaurantPosition(ACTIVE, crawlScope.getDescription());
    }

    private List<String> createNewCandidates(Set<String> rawPlaceIds, Set<String> restaurantPlaceIds) {
        List<String> created = new ArrayList<>();
        for (String placeId : rawPlaceIds) {
            if (restaurantPlaceIds.contains(placeId)) {
                continue;
            }
            if (createPendingCandidateIfAbsent(placeId, SyncCandidateType.NEW, null)) {
                created.add(placeId);
            }
        }
        return created;
    }

    private List<String> createClosedCandidates(Set<String> rawPlaceIds, Set<String> restaurantPlaceIds) {
        List<String> created = new ArrayList<>();
        for (String placeId : restaurantPlaceIds) {
            if (rawPlaceIds.contains(placeId)) {
                continue;
            }
            if (createPendingCandidateIfAbsent(placeId, SyncCandidateType.CLOSED, null)) {
                created.add(placeId);
            }
        }
        return created;
    }

    private boolean createPendingCandidateIfAbsent(String placeId, SyncCandidateType type, String reason) {
        boolean exists = candidateRepository.existsByPlaceIdAndCandidateTypeAndCandidateStatus(
                placeId,
                type,
                SyncCandidateStatus.PENDING
        );
        if (exists) {
            return false;
        }
        candidateRepository.save(RestaurantSyncCandidateEntity.pending(placeId, type, reason));
        return true;
    }

    private List<String> updateIntersectedRestaurants(
            Map<String, RestaurantCrawlRawEntity> rawByPlaceId,
            Map<String, RestaurantEntity> restaurantByPlaceId,
            Map<Long, List<RestaurantMenuCrawlRawEntity>> rawMenusByRawId
    ) {
        List<String> updatedPlaceIds = new ArrayList<>();
        for (Map.Entry<String, RestaurantCrawlRawEntity> entry : rawByPlaceId.entrySet()) {
            String placeId = entry.getKey();
            RestaurantEntity restaurant = restaurantByPlaceId.get(placeId);
            if (restaurant == null) {
                continue;
            }

            RestaurantCrawlRawEntity raw = entry.getValue();
            List<RestaurantMenuCrawlRawEntity> rawMenus = rawMenusByRawId.getOrDefault(raw.getId(), List.of());
            String contentHash = computeContentHash(raw);
            String menuHash = computeMenuHash(rawMenus);
            boolean changed = !Objects.equals(restaurant.getContentHash(), contentHash)
                    || !Objects.equals(restaurant.getMenuHash(), menuHash)
                    || !ACTIVE.equals(restaurant.getStatus());

            if (!changed) {
                continue;
            }

            restaurant.applyRaw(
                    normalize(raw.getPlaceName(), "UNKNOWN_PLACE"),
                    normalize(raw.getCategory(), UNKNOWN_CATEGORY),
                    raw.getCrawlScope() == null ? null : raw.getCrawlScope().getDescription(),
                    raw.getRestaurantAddress(),
                    raw.getPhoneNumber(),
                    raw.getImageUrl(),
                    normalize(raw.getCategory(), UNKNOWN_CATEGORY),
                    raw.getLatitude(),
                    raw.getLongitude()
            );
            restaurant.updateHashes(contentHash, menuHash);
            restaurant.markActive();
            replaceRestaurantMenus(restaurant.getRestaurantId(), rawMenus);
            updatedPlaceIds.add(placeId);
        }
        return updatedPlaceIds;
    }

    private void applyNewRestaurant(String placeId, String manualCuisine) {
        RestaurantCrawlRawEntity raw = rawRepository.findBySourcePlaceId(placeId)
                .filter(entity -> SUCCESS.equals(entity.getCrawlStatus()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "raw not found for placeId=" + placeId));

        List<RestaurantMenuCrawlRawEntity> rawMenus = menuRawRepository.findAllByRestaurantRawIdIn(List.of(raw.getId()));
        String contentHash = computeContentHash(raw);
        String menuHash = computeMenuHash(rawMenus);
        String rawType = normalize(raw.getCategory(), UNKNOWN_CATEGORY);
        String mappedCuisine = resolveCuisineForNew(raw.getCategory(), manualCuisine);

        RestaurantEntity restaurant = restaurantRepository.findByPlaceId(placeId)
                .orElseGet(() -> restaurantRepository.save(new RestaurantEntity(
                        null,
                        normalize(raw.getPlaceName(), "UNKNOWN_PLACE"),
                        rawType,
                        raw.getCrawlScope() == null ? null : raw.getCrawlScope().getDescription(),
                        raw.getRestaurantAddress(),
                        raw.getPhoneNumber(),
                        raw.getSourcePlaceId(),
                        raw.getImageUrl(),
                        0,
                        mappedCuisine,
                        raw.getLatitude(),
                        raw.getLongitude(),
                        null,
                        ACTIVE,
                        contentHash,
                        menuHash
                )));

        restaurant.applyRaw(
                normalize(raw.getPlaceName(), "UNKNOWN_PLACE"),
                rawType,
                raw.getCrawlScope() == null ? null : raw.getCrawlScope().getDescription(),
                raw.getRestaurantAddress(),
                raw.getPhoneNumber(),
                raw.getImageUrl(),
                mappedCuisine,
                raw.getLatitude(),
                raw.getLongitude()
        );
        restaurant.updateHashes(contentHash, menuHash);
        restaurant.markActive();
        replaceRestaurantMenus(restaurant.getRestaurantId(), rawMenus);
    }

    private void applyClosedRestaurant(String placeId) {
        RestaurantEntity restaurant = restaurantRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "restaurant not found for placeId=" + placeId));
        restaurant.markInactive();
        restaurantRepository.save(restaurant);
    }

    private String toLookupPlaceId(String placeId) {
        if (placeId == null) {
            return "";
        }
        int underscoreIndex = placeId.indexOf('_');
        if (underscoreIndex <= 0) {
            return placeId;
        }
        return placeId.substring(0, underscoreIndex);
    }

    private boolean isClosedByNotFoundMessage(RestaurantRaw analyzed) {
        if (analyzed == null) {
            return false;
        }
        String address = analyzed.restaurantAddress();
        return address != null && address.contains(NAVER_PLACE_NOT_FOUND_TEXT);
    }

    private void replaceRestaurantMenus(Long restaurantId, Collection<RestaurantMenuCrawlRawEntity> rawMenus) {
        restaurantMenuRepository.deleteByRestaurantId(restaurantId);
        if (rawMenus == null || rawMenus.isEmpty()) {
            return;
        }
        List<RestaurantMenuEntity> menus = rawMenus.stream()
                .map(menu -> RestaurantMenuEntity.of(
                        restaurantId,
                        normalize(menu.getMenuName(), "UNKNOWN_MENU"),
                        menu.getMenuPrice(),
                        menu.getMenuImageUrl()
                ))
                .toList();
        restaurantMenuRepository.saveAll(menus);
    }

    private RestaurantSyncCandidateResponse toResponse(
            RestaurantSyncCandidateEntity entity,
            RestaurantEntity restaurant,
            RestaurantCrawlRawEntity raw
    ) {
        String restaurantName = restaurant != null
                ? restaurant.getRestaurantName()
                : (raw != null ? raw.getPlaceName() : entity.getPlaceId());
        String restaurantType = restaurant != null
                ? restaurant.getRestaurantType()
                : (raw != null ? raw.getCategory() : null);
        String mappedCuisine = mapRawCategoryToFixedCuisine(restaurantType);
        if (mappedCuisine == null && raw != null) {
            mappedCuisine = mapRawCategoryToFixedCuisine(raw.getCategory());
        }
        String restaurantLink = "https://map.naver.com/p/entry/place/" + entity.getPlaceId();

        return new RestaurantSyncCandidateResponse(
                entity.getId(),
                entity.getPlaceId(),
                restaurantName,
                normalize(restaurantType, "-"),
                normalize(mappedCuisine, "-"),
                restaurantLink,
                entity.getCandidateType(),
                entity.getCandidateStatus(),
                entity.getReason(),
                entity.getReviewedBy(),
                entity.getReviewedAt(),
                entity.getAppliedAt(),
                entity.getCreatedAt()
        );
    }

    private String resolveCuisineForNew(String rawCategory, String manualCuisine) {
        String normalizedManual = normalizeToNull(manualCuisine);
        if (normalizedManual != null && !FIXED_CUISINES.contains(normalizedManual)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid manualCuisine: " + manualCuisine);
        }

        String autoMapped = mapRawCategoryToFixedCuisine(rawCategory);
        if (autoMapped != null) {
            return autoMapped;
        }
        if (normalizedManual != null) {
            return normalizedManual;
        }
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "자동 매핑 실패: manualCuisine(고정 식당종류) 값을 지정해주세요."
        );
    }

    private String mapRawCategoryToFixedCuisine(String rawCategory) {
        String normalized = normalizeToNull(rawCategory);
        if (normalized == null) {
            return null;
        }
        if (FIXED_CUISINES.contains(normalized)) {
            return normalized;
        }
        if (containsAny(normalized, "찜닭", "백반", "한정식", "국밥", "냉면", "삼계탕", "해장국")) return "한식";
        if (containsAny(normalized, "스시", "초밥", "라멘", "우동", "소바", "돈카츠", "돈까스", "돈가스")) return "일식";
        if (containsAny(normalized, "중식", "중국집", "짜장", "짬뽕", "탕수육", "마라", "훠궈")) return "중식";
        if (containsAny(normalized, "파스타", "스테이크", "리조또", "브런치", "이탈리안", "프렌치")) return "양식";
        if (containsAny(normalized, "아시안", "태국", "타이", "베트남", "쌀국수", "인도", "동남아")) return "아시안";
        if (containsAny(normalized, "고깃집", "삼겹살", "갈비", "곱창", "대창", "막창", "육회", "육류", "양꼬치", "족발", "보쌈", "소고기", "돼지고기")) return "고기";
        if (containsAny(normalized, "치킨", "닭강정", "통닭", "후라이드", "양념치킨", "닭꼬치")) return "치킨";
        if (containsAny(normalized, "해산물", "횟집", "숙성회", "해물", "조개", "대게", "킹크랩", "주꾸미", "생선회")) return "해산물";
        if (containsAny(normalized, "햄버거", "버거", "피자", "핫도그")) return "햄버거/피자";
        if (containsAny(normalized, "분식", "떡볶이", "김밥", "쫄면", "순대", "어묵")) return "분식";
        if (containsAny(normalized, "술집", "주점", "포차", "호프", "펍", "와인바", "이자카야", "바", "포장마차")) return "술집";
        if (containsAny(normalized, "카페", "커피", "디저트", "빙수", "도넛", "젤라또", "마카롱")) return "카페/디저트";
        if (containsAny(normalized, "베이커리", "빵집", "제과", "제빵", "크루아상", "바게트")) return "베이커리";
        if (containsAny(normalized, "샐러드", "포케", "그레인볼", "비건볼")) return "샐러드";
        return null;
    }

    private boolean containsAny(String value, String... keywords) {
        for (String keyword : keywords) {
            if (value.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String computeContentHash(RestaurantCrawlRawEntity raw) {
        String serialized = String.join("|",
                normalize(raw.getSourcePlaceId(), ""),
                normalize(raw.getPlaceName(), ""),
                normalize(raw.getCategory(), ""),
                normalize(raw.getRestaurantAddress(), ""),
                normalize(raw.getPhoneNumber(), ""),
                normalize(raw.getImageUrl(), ""),
                String.valueOf(raw.getLatitude()),
                String.valueOf(raw.getLongitude()),
                raw.getCrawlScope() == null ? "" : raw.getCrawlScope().name()
        );
        return sha256(serialized);
    }

    private String computeMenuHash(List<RestaurantMenuCrawlRawEntity> menus) {
        if (menus == null || menus.isEmpty()) {
            return sha256("");
        }
        List<String> lines = menus.stream()
                .sorted(Comparator.comparing(menu -> normalize(menu.getMenuName(), "")))
                .map(menu -> String.join("|",
                        normalize(menu.getMenuName(), ""),
                        normalize(menu.getMenuPrice(), ""),
                        normalize(menu.getMenuImageUrl(), "")
                ))
                .toList();
        return sha256(String.join("\n", lines));
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
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
