package com.kustaurant.restaurantSync.sync;

import java.util.List;

public record ZoneCrawlStatusPayload(
        // 서버 간 통신용 -> 쿠스토랑 서버가 폴링으로 현재 작업 진행상태 받아오는 용
        ZoneJCrawlobStatus status,
        int totalGridCount,
        int processedGridCount,
        int discoveredPlaceCount,
        int attemptedPlaceCount,
        int crawledSuccessCount,
        int finalFailedCount,
        List<String> finalFailedPlaceIds,
        String currentGrid,
        String currentPlaceId,
        String errorMessage
) {}
