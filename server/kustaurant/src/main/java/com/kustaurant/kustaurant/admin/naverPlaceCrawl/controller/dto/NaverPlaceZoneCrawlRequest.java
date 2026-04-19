package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.dto;

import com.kustaurant.naverplace.CrawlScopeType;
import jakarta.validation.constraints.NotNull;

public record NaverPlaceZoneSyncRequest(@NotNull CrawlScopeType crawlScope) {
}
