package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawCrawlResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawMenuResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverPlaceAnalyzeService {

    private final RestaurantCrawlerClient crawlerClient;

    public NaverPlaceRawCrawlResponse analyze(String placeUrl) {
        NaverPlaceCrawlResult result = crawlerClient.analyzeOne(placeUrl);
        if (result == null) {
            throw new IllegalStateException("analyze result is null");
        }
        List<NaverPlaceRawMenuResponse> menus = result.menus() == null
                ? List.of()
                : result.menus().stream()
                .map(menu -> new NaverPlaceRawMenuResponse(
                        menu.menuName(),
                        menu.menuPrice(),
                        menu.menuImageUrl()
                ))
                .toList();

        log.info(
                "naver place analyze summary. sourcePlaceId={}, sourceUrl={}, placeName={}, menuCount={}",
                result.sourcePlaceId(),
                result.sourceUrl(),
                result.placeName(),
                menus.size()
        );

        return new NaverPlaceRawCrawlResponse(
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
                menus.size(),
                menus
        );
    }
}
