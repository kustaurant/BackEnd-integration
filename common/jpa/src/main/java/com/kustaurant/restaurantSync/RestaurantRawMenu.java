package com.kustaurant.restaurantSync;

public record RestaurantRawMenu(
        String menuName,
        String menuPrice,
        String menuImageUrl
) {}
