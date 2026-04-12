package com.kustaurant.crawler.RestaurantSync.service;

import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverPlaceAnalyzeCrawler {

    private final NaverPlaceCrawler crawler;

    public NaverPlaceCrawlResult analyze(String placeUrl) {
        return crawler.analyze(placeUrl);
    }
}
