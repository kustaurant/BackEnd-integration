package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command;

import com.kustaurant.map.ZoneType;
import java.util.List;

public record RestaurantCrawlResponse(
        Long rawId,
        String sourcePlaceId,
        String sourceUrl,
        String placeName,
        String category,
        String restaurantAddress,
        String phoneNumber,
        Double latitude,
        Double longitude,
        String imageUrl,
        ZoneType crawlScope,
        String crawlScopeDescription,
        int menuCount,
        List<RestaurantRawMenuResponse> menus
) {}
