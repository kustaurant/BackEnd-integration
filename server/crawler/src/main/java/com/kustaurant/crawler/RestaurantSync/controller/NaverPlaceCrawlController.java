package com.kustaurant.crawler.RestaurantSync.controller;

import com.kustaurant.crawler.RestaurantSync.service.NaverPlaceAnalyzeCrawler;
import com.kustaurant.crawler.RestaurantSync.service.NaverPlaceCrawler;
import com.kustaurant.naverplace.NaverPlaceCrawlRequest;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/naver-place")
public class NaverPlaceCrawlController {

    private final NaverPlaceCrawler crawler;
    private final NaverPlaceAnalyzeCrawler analyzeCrawler;

    @PostMapping("/crawl-one")
    public NaverPlaceCrawlResult crawlOne(@RequestBody NaverPlaceCrawlRequest request) {
        return crawler.crawl(request.placeUrl());
    }

    @PostMapping("/analyze-one")
    public NaverPlaceCrawlResult analyzeOne(@RequestBody NaverPlaceCrawlRequest request) {
        return analyzeCrawler.analyze(request.placeUrl());
    }
}
