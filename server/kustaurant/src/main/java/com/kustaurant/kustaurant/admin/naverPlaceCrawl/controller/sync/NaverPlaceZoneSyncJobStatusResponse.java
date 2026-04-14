package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync;

import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.sync.NaverPlaceZoneJobStatus;
import java.time.LocalDateTime;

public record NaverPlaceZoneSyncJobStatusResponse(String jobId, CrawlScopeType crawlScope, NaverPlaceZoneJobStatus status, String currentPhase, int totalGridCount, int processedGridCount, int discoveredPlaceCount, int totalPlaceCount, int attemptedPlaceCount, int crawledSuccessCount, int savedRawCount, int saveFailedCount, String currentGrid, String currentPlaceId, String errorMessage, LocalDateTime startedAt, LocalDateTime finishedAt) {
}
