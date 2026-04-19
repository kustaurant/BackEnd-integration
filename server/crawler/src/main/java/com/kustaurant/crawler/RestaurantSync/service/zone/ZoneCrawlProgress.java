package com.kustaurant.crawler.RestaurantSync.service.zone;

import com.kustaurant.restaurantSync.RestaurantRaw;

import java.util.List;

public record ZoneCrawlProgress(
        String phase,
        int totalGridCount,
        int processedGridCount,
        int discoveredPlaceCount,
        int totalPlaceCount,
        int attemptedPlaceCount,
        int crawledSuccessCount,
        int finalFailedCount,
        List<String> finalFailedPlaceIds,
        RestaurantRaw acceptedResult,
        String currentGrid,
        String currentPlaceId
) {
}