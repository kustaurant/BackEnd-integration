package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.dto;

import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.sync.ZoneJCrawlobStatus;
import java.util.List;

public record ZoneCrawlJobStatusResponse(
        // 프론트 렌더링용
        ZoneType crawlScope,
        ZoneJCrawlobStatus status,
        String currentPhase,
        int totalGridCount,
        int processedGridCount,
        int discoveredPlaceCount,
        int attemptedPlaceCount,
        int crawledSuccessCount,
        int finalFailedCount,
        List<String> finalFailedPlaceIds,
        int savedRawCount,
        int saveFailedCount,
        String currentGrid,
        String currentPlaceId,
        String errorMessage
) {
}
