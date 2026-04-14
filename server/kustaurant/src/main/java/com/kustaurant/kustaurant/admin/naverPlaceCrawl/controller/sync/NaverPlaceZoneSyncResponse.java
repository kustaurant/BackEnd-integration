package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync;

import com.kustaurant.naverplace.CrawlScopeType;

public record NaverPlaceZoneSyncResponse(CrawlScopeType crawlScope, int discoveredPlaceCount, int crawledSuccessCount, int savedRawCount) {
}
