package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto;

public record ZoneCrawlResultResponse(
        // 프론트 렌더링용
        int discoveredPlaceCount,
        int crawledSuccessCount,
        int savedRawCount
) {}
