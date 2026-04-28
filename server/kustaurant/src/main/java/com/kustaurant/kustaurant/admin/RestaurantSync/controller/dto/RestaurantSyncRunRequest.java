package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

import com.kustaurant.map.ZoneType;

public record RestaurantSyncRunRequest(
        ZoneType crawlScope
) {
}
