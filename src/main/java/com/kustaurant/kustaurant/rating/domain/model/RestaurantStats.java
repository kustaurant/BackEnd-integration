package com.kustaurant.kustaurant.rating.domain.model;

public record RestaurantStats(
        int restaurantId,
        int visitCount,
        int favoriteCount,
        int evaluationCount
) {

}
