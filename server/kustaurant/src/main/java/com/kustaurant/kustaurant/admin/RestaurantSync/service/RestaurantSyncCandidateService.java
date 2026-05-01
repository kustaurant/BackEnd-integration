package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.NewCandidateAutoApproveResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateActionResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncCandidateResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncRunResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto.RestaurantSyncUpdatedItemResponse;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateStatus;
import com.kustaurant.kustaurant.admin.RestaurantSync.domain.SyncCandidateType;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateEntity;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantSyncCandidateRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantJpaRepository;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurant.entity.RestaurantEntity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
// 후보 생성/조회/승인/반려 및 신규 후보 일괄 승인 흐름을 담당하는 서비스.
public class RestaurantSyncCandidateService {

    private static final String SUCCESS = "SUCCESS";
    private static final String ACTIVE = "ACTIVE";

    private final RestaurantCrawlRawRepository rawRepository;
    private final RestaurantMenuRawRepository menuRawRepository;
    private final RestaurantJpaRepository restaurantRepository;
    private final RestaurantSyncCandidateRepository candidateRepository;
    private final RestaurantSyncApplyService applyService;
    private final CuisineMappingService cuisineMappingService;

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
        List<String> updatedPlaceIds = applyService.updateIntersectedRestaurants(rawByPlaceId, restaurantByPlaceId, rawMenusByRawId);
        List<RestaurantSyncUpdatedItemResponse> updatedRestaurants = updatedPlaceIds.stream()
                .map(placeId -> {
                    RestaurantEntity restaurant = restaurantByPlaceId.get(placeId);
                    if (restaurant == null) {
                        return new RestaurantSyncUpdatedItemResponse(placeId, placeId, "https://map.naver.com/p/entry/place/" + placeId);
                    }
                    return new RestaurantSyncUpdatedItemResponse(placeId, restaurant.getRestaurantName(), "/restaurants/" + restaurant.getRestaurantId());
                }).toList();

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
        Set<String> placeIds = candidates.stream().map(RestaurantSyncCandidateEntity::getPlaceId).collect(Collectors.toSet());
        if (placeIds.isEmpty()) return List.of();

        Map<String, RestaurantEntity> restaurantsByPlaceId = restaurantRepository.findAllByPlaceIdIn(placeIds)
                .stream().collect(Collectors.toMap(RestaurantEntity::getPlaceId, Function.identity(), (a, b) -> a));
        Map<String, RestaurantCrawlRawEntity> rawsByPlaceId = rawRepository.findAllBySourcePlaceIdIn(placeIds)
                .stream().collect(Collectors.toMap(RestaurantCrawlRawEntity::getSourcePlaceId, Function.identity(), (a, b) -> a));

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
            applyService.applyNewRestaurant(candidate.getPlaceId(), manualCuisine);
        } else {
            applyService.applyClosedRestaurant(candidate.getPlaceId());
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
    public NewCandidateAutoApproveResponse autoApproveNewCandidates(String reviewedBy) {
        List<RestaurantSyncCandidateEntity> newCandidates = candidateRepository.findAllByCandidateStatusOrderByCreatedAtDesc(SyncCandidateStatus.PENDING)
                .stream().filter(candidate -> candidate.getCandidateType() == SyncCandidateType.NEW).toList();
        int approvedCount = 0;
        int failedCount = 0;
        for (RestaurantSyncCandidateEntity candidate : newCandidates) {
            try {
                applyService.applyNewRestaurant(candidate.getPlaceId(), null);
                candidateRepository.delete(candidate);
                approvedCount++;
            } catch (Exception e) {
                failedCount++;
                log.info("신규 후보 자동 승인 실패. candidateId={}, placeId={}, reason={}",
                        candidate.getId(), candidate.getPlaceId(), e.getMessage());
            }
        }
        return new NewCandidateAutoApproveResponse(newCandidates.size(), approvedCount, failedCount);
    }

    private List<RestaurantCrawlRawEntity> loadRaws(ZoneType crawlScope) {
        if (crawlScope == null) return rawRepository.findAllByCrawlStatus(SUCCESS);
        return rawRepository.findAllByCrawlStatusAndCrawlScope(SUCCESS, crawlScope);
    }

    private Map<Long, List<RestaurantMenuCrawlRawEntity>> loadRawMenus(List<RestaurantCrawlRawEntity> raws) {
        if (raws.isEmpty()) return Map.of();
        List<Long> rawIds = raws.stream().map(RestaurantCrawlRawEntity::getId).toList();
        return menuRawRepository.findAllByRestaurantRawIdIn(rawIds).stream()
                .collect(Collectors.groupingBy(RestaurantMenuCrawlRawEntity::getRestaurantRawId));
    }

    private List<RestaurantEntity> loadRestaurants(ZoneType crawlScope) {
        if (crawlScope == null) return restaurantRepository.findAllByStatus(ACTIVE);
        return restaurantRepository.findAllByStatusAndRestaurantPosition(ACTIVE, crawlScope.getDescription());
    }

    private List<String> createNewCandidates(Set<String> rawPlaceIds, Set<String> restaurantPlaceIds) {
        List<String> created = new ArrayList<>();
        for (String placeId : rawPlaceIds) {
            if (restaurantPlaceIds.contains(placeId)) continue;
            if (createPendingCandidateIfAbsent(placeId, SyncCandidateType.NEW, null)) created.add(placeId);
        }
        return created;
    }

    private List<String> createClosedCandidates(Set<String> rawPlaceIds, Set<String> restaurantPlaceIds) {
        List<String> created = new ArrayList<>();
        for (String placeId : restaurantPlaceIds) {
            if (rawPlaceIds.contains(placeId)) continue;
            if (createPendingCandidateIfAbsent(placeId, SyncCandidateType.CLOSED, null)) created.add(placeId);
        }
        return created;
    }

    private boolean createPendingCandidateIfAbsent(String placeId, SyncCandidateType type, String reason) {
        boolean exists = candidateRepository.existsByPlaceIdAndCandidateTypeAndCandidateStatus(placeId, type, SyncCandidateStatus.PENDING);
        if (exists) return false;
        candidateRepository.save(RestaurantSyncCandidateEntity.pending(placeId, type, reason));
        return true;
    }

    private RestaurantSyncCandidateResponse toResponse(
            RestaurantSyncCandidateEntity entity,
            RestaurantEntity restaurant,
            RestaurantCrawlRawEntity raw
    ) {
        String restaurantName = restaurant != null ? restaurant.getRestaurantName() : (raw != null ? raw.getPlaceName() : entity.getPlaceId());
        String restaurantType = restaurant != null ? restaurant.getRestaurantType() : (raw != null ? raw.getCategory() : null);
        String mappedCuisine = cuisineMappingService.mapRawCategoryToFixedCuisine(restaurantType);
        if (mappedCuisine == null && raw != null) {
            mappedCuisine = cuisineMappingService.mapRawCategoryToFixedCuisine(raw.getCategory());
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

    private String normalize(String value, String fallback) {
        if (value == null) return fallback;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}
