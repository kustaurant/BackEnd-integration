package com.kustaurant.kustaurant.admin.RestaurantSync.controller.dto;

import java.util.List;

public record RestaurantSyncRunResponse(
        int rawCount,
        int existingCount,
        int newCandidateCount,
        int closedCandidateCount,
        int updatedRestaurantCount,
        List<String> newCandidatePlaceIds,
        List<String> closedCandidatePlaceIds,
        List<String> updatedPlaceIds,
        List<RestaurantSyncUpdatedItemResponse> updatedRestaurants
) {
}
