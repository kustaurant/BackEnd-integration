package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

import java.util.List;

public record NaverPlaceRawCrawlResponse(
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
        int menuCount,
        List<NaverPlaceRawMenuResponse> menus
) {}
