package com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command;

public record RestaurantRawMenuResponse(
        String menuName,
        String menuPrice,
        String menuImageUrl
) {}
