package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.dto;

import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.sync.NaverPlaceZoneJobStatus;

public record NaverPlaceZoneSyncJobStartResponse(String jobId, CrawlScopeType crawlScope, NaverPlaceZoneJobStatus status) {
}
