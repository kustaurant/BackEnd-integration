package com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response;

public record RestaurantForSearch(
        long id,
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
