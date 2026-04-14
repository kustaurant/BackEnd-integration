package com.kustaurant.naverplace.sync;

import com.kustaurant.naverplace.CrawlScopeType;
import java.time.LocalDateTime;

public record NaverPlaceZoneCrawlJobStatusResponse(String jobId, CrawlScopeType crawlScope, NaverPlaceZoneJobStatus status, String currentPhase, int totalGridCount, int processedGridCount, int discoveredPlaceCount, int totalPlaceCount, int attemptedPlaceCount, int crawledSuccessCount, String currentGrid, String currentPlaceId, String errorMessage, LocalDateTime startedAt, LocalDateTime finishedAt) {
}
