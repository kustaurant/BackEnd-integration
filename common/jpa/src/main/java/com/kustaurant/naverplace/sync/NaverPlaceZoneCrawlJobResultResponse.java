package com.kustaurant.naverplace.sync;

import com.kustaurant.naverplace.CrawlScopeType;

public record NaverPlaceZoneCrawlJobResultResponse(String jobId, CrawlScopeType crawlScope, NaverPlaceZoneJobStatus status, NaverPlaceZoneCrawlResult result) {
}
