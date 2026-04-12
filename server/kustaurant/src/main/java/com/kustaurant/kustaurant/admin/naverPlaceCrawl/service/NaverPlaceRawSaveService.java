package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawCrawlResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawMenuResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.NaverPlaceMenu;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaverPlaceRawSaveService {

    private final RestaurantCrawlerClient crawlerClient;
    private final RestaurantCrawlRawRepository rawRepository;
    private final RestaurantMenuRawRepository menuRawRepository;
    private final NaverPlaceUrlValidator urlValidator;

    @Transactional
    public NaverPlaceRawCrawlResponse crawlAndSave(String placeUrl) {
        urlValidator.validateOrThrow(placeUrl);
        NaverPlaceCrawlResult result = crawlerClient.crawlOne(placeUrl);

        deleteExistingRawByPlaceIdOrUrl(result);

        RestaurantCrawlRawEntity rawEntity = rawRepository.save(
                RestaurantCrawlRawEntity.success(
                        result.sourcePlaceId(),
                        result.sourceUrl(),
                        defaultIfBlank(result.placeName(), "UNKNOWN_PLACE"),
                        result.category(),
                        result.restaurantAddress(),
                        result.phoneNumber(),
                        result.latitude(),
                        result.longitude(),
                        result.imageUrl(),
                        CrawlScopeType.SINGLE
                )
        );

        List<NaverPlaceRawMenuResponse> menus = saveMenus(rawEntity.getId(), result.menus());

        return new NaverPlaceRawCrawlResponse(
                rawEntity.getId(),
                rawEntity.getSourcePlaceId(),
                rawEntity.getSourceUrl(),
                rawEntity.getPlaceName(),
                rawEntity.getCategory(),
                rawEntity.getRestaurantAddress(),
                rawEntity.getPhoneNumber(),
                rawEntity.getLatitude(),
                rawEntity.getLongitude(),
                rawEntity.getImageUrl(),
                menus.size(),
                menus
        );
    }

    private List<NaverPlaceRawMenuResponse> saveMenus(Long rawId, List<NaverPlaceMenu> menus) {
        if (menus == null || menus.isEmpty()) {
            return List.of();
        }

        List<RestaurantMenuCrawlRawEntity> entities = menus.stream()
                .map(menu -> RestaurantMenuCrawlRawEntity.of(
                        rawId,
                        defaultIfBlank(menu.menuName(), "UNKNOWN_MENU"),
                        menu.menuPrice(),
                        menu.menuImageUrl()
                ))
                .toList();

        menuRawRepository.saveAll(entities);

        return menus.stream()
                .map(menu -> new NaverPlaceRawMenuResponse(
                        menu.menuName(),
                        menu.menuPrice(),
                        menu.menuImageUrl()
                ))
                .toList();
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void deleteExistingRawByPlaceIdOrUrl(NaverPlaceCrawlResult result) {
        List<RestaurantCrawlRawEntity> targets = new ArrayList<>();

        if (result.sourcePlaceId() != null && !result.sourcePlaceId().isBlank()) {
            rawRepository.findBySourcePlaceId(result.sourcePlaceId()).ifPresent(targets::add);
        }

        if (result.sourceUrl() != null && !result.sourceUrl().isBlank()) {
            rawRepository.findBySourceUrl(result.sourceUrl())
                    .filter(entity -> targets.stream().noneMatch(t -> t.getId().equals(entity.getId())))
                    .ifPresent(targets::add);
        }

        for (RestaurantCrawlRawEntity target : targets) {
            menuRawRepository.deleteByRestaurantRawId(target.getId());
            rawRepository.delete(target);
        }

        // Ensure deletes are committed before next insert to avoid source_place_id unique collision.
        if (!targets.isEmpty()) {
            rawRepository.flush();
        }
    }
}
