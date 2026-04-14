package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.sync;

import com.kustaurant.naverplace.CrawlScopeType;
import jakarta.validation.constraints.NotNull;

public record NaverPlaceZoneSyncRequest(@NotNull CrawlScopeType crawlScope) {
}
