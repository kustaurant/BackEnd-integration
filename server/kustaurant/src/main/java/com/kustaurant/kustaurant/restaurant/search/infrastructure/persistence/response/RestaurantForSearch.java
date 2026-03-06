package com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response;

public record RestaurantForSearch(
        String name,
        String cuisine,
        String position,
        String imgUrl,
        int tier,
        String partnershipInfo,
        boolean isEvaluated,
        boolean isFavorite
) {
}
