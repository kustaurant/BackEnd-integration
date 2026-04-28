package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command.RestaurantCrawlResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command.RestaurantRawMenuResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.map.ZoneType;

import java.util.List;

import com.kustaurant.restaurantSync.RestaurantRaw;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantAnalyzeService {

    private final RestaurantCrawlerClient crawlerClient;

    public RestaurantCrawlResponse analyze(String placeId) {
        RestaurantRaw result = crawlerClient.analyzeOne(placeId);
        if (result == null) throw new IllegalStateException("analyze result is null");

        List<RestaurantRawMenuResponse> menus = result.menus() == null ? List.of() : result.menus().stream()
                .map(menu -> new RestaurantRawMenuResponse(
                        menu.menuName(),
                        menu.menuPrice(),
                        menu.menuImageUrl()
                ))
                .toList();
        ZoneType zoneType = result.crawlScope() == null ? ZoneType.OUT_OF_ZONE : result.crawlScope();

        log.info(
                "네이버플레이스 단건 분석 완료. sourcePlaceId={}, sourceUrl={}, placeName={}, lat={}, lng={}, menuCount={}, zoneType={}, zoneDescription={}",
                result.sourcePlaceId(), result.sourceUrl(), result.placeName(),
                result.latitude(), result.longitude(), menus.size(), zoneType, zoneType.getDescription()
        );

        return new RestaurantCrawlResponse(
                null,
                result.sourcePlaceId(),
                result.sourceUrl(),
                result.placeName(),
                result.category(),
                result.restaurantAddress(),
                result.phoneNumber(),
                result.latitude(),
                result.longitude(),
                result.imageUrl(),
                zoneType,
                zoneType.getDescription(),
                menus.size(),
                menus
        );
    }
}
