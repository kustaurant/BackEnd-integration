package com.kustaurant.kustaurant.restaurant.application.service.command.port;

import com.kustaurant.kustaurant.restaurant.domain.RestaurantMenu;

import java.util.List;

public interface RestaurantMenuRepository {

    List<RestaurantMenu> findByRestaurantOrderById(Integer restaurantId);
}
