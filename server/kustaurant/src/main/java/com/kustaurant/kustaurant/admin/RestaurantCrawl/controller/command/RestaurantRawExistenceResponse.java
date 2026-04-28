package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command;

import com.kustaurant.map.ZoneType;

public record RestaurantRawExistenceResponse(
        boolean exists,
        String sourcePlaceId,
        ZoneType crawlScope,
        String crawlScopeDescription,
        String placeName
) {
}
