package com.kustaurant.kustaurant.admin.RestaurantSync.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantSync.infrastructure.RestaurantMenuJpaRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository.RestaurantJpaRepository;
import com.kustaurant.restaurant.entity.RestaurantEntity;
import com.kustaurant.restaurant.entity.RestaurantMenuEntity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
// raw 데이터를 운영 식당 데이터에 반영(신규/폐점/업데이트)하는 적용 전용 서비스.
public class RestaurantSyncApplyService {

    private static final String SUCCESS = "SUCCESS";
    private static final String ACTIVE = "ACTIVE";
    private static final String UNKNOWN_CATEGORY = "기타";

    private final RestaurantCrawlRawRepository rawRepository;
    private final RestaurantMenuRawRepository menuRawRepository;
    private final RestaurantJpaRepository restaurantRepository;
    private final RestaurantMenuJpaRepository restaurantMenuRepository;
    private final CuisineMappingService cuisineMappingService;

    @Transactional
    public void applyNewRestaurant(String placeId, String manualCuisine) {
        RestaurantCrawlRawEntity raw = rawRepository.findBySourcePlaceId(placeId)
                .filter(entity -> SUCCESS.equals(entity.getCrawlStatus()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "raw not found for placeId=" + placeId));

        List<RestaurantMenuCrawlRawEntity> rawMenus = menuRawRepository.findAllByRestaurantRawIdIn(List.of(raw.getId()));
        String contentHash = computeContentHash(raw);
        String menuHash = computeMenuHash(rawMenus);
        String rawType = normalize(raw.getCategory(), UNKNOWN_CATEGORY);
        String mappedCuisine = cuisineMappingService.resolveCuisineForNew(raw.getCategory(), manualCuisine);

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

    @Transactional
    public void applyClosedRestaurant(String placeId) {
        RestaurantEntity restaurant = restaurantRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "restaurant not found for placeId=" + placeId));
        restaurant.markInactive();
        restaurantRepository.save(restaurant);
    }

    @Transactional
    public List<String> updateIntersectedRestaurants(
            Map<String, RestaurantCrawlRawEntity> rawByPlaceId,
            Map<String, RestaurantEntity> restaurantByPlaceId,
            Map<Long, List<RestaurantMenuCrawlRawEntity>> rawMenusByRawId
    ) {
        List<String> updatedPlaceIds = new ArrayList<>();
        for (Map.Entry<String, RestaurantCrawlRawEntity> entry : rawByPlaceId.entrySet()) {
            String placeId = entry.getKey();
            RestaurantEntity restaurant = restaurantByPlaceId.get(placeId);
            if (restaurant == null) continue;

            RestaurantCrawlRawEntity raw = entry.getValue();
            List<RestaurantMenuCrawlRawEntity> rawMenus = rawMenusByRawId.getOrDefault(raw.getId(), List.of());
            String contentHash = computeContentHash(raw);
            String menuHash = computeMenuHash(rawMenus);
            boolean changed = !Objects.equals(restaurant.getContentHash(), contentHash)
                    || !Objects.equals(restaurant.getMenuHash(), menuHash)
                    || !ACTIVE.equals(restaurant.getStatus());
            if (!changed) continue;

            restaurant.applyRaw(
                    normalize(raw.getPlaceName(), "UNKNOWN_PLACE"),
                    normalize(raw.getCategory(), UNKNOWN_CATEGORY),
                    raw.getCrawlScope() == null ? null : raw.getCrawlScope().getDescription(),
                    raw.getRestaurantAddress(),
                    raw.getPhoneNumber(),
                    raw.getImageUrl(),
                    restaurant.getRestaurantCuisine(),
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

    private void replaceRestaurantMenus(Long restaurantId, Collection<RestaurantMenuCrawlRawEntity> rawMenus) {
        restaurantMenuRepository.deleteByRestaurantId(restaurantId);
        if (rawMenus == null || rawMenus.isEmpty()) return;

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
        if (menus == null || menus.isEmpty()) return sha256("");
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
        if (value == null) return fallback;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}
