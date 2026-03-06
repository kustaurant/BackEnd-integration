package com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response;

import java.util.List;

public record RestaurantForEngine(
        long id,
        String name,
        String cuisine,
        List<String> menus
) {
}
