package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

public record RestaurantSyncUpdatedItemResponse(
        String placeId,
        String restaurantName,
        String restaurantLink
) {
}
