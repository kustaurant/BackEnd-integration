package com.kustaurant.naverplace.sync;

import com.kustaurant.naverplace.CrawlScopeType;

public record NaverPlaceZoneCrawlJobStartResponse(String jobId, CrawlScopeType crawlScope, NaverPlaceZoneJobStatus status) {
}
