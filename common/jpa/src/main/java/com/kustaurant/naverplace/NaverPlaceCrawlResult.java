package com.kustaurant.naverplace;

import java.util.List;

public record NaverPlaceCrawlResult(
        String sourcePlaceId,
        String sourceUrl,
        String placeName,
        String category,
        String restaurantAddress,
        String phoneNumber,
        Double latitude,
        Double longitude,
        String imageUrl,
        List<NaverPlaceMenu> menus
) {}
