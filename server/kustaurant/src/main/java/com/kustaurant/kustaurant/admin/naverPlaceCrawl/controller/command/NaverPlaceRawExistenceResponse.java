package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

import com.kustaurant.naverplace.CrawlScopeType;

public record NaverPlaceRawExistenceResponse(
        boolean exists,
        String sourcePlaceId,
        CrawlScopeType crawlScope,
        String crawlScopeDescription,
        String placeName
) {
}
