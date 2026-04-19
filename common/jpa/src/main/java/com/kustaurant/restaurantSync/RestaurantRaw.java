package com.kustaurant.restaurantSync;

import com.kustaurant.map.ZoneType;
import java.util.List;

public record RestaurantRaw(
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
        List<RestaurantRawMenu> menus
) {}
