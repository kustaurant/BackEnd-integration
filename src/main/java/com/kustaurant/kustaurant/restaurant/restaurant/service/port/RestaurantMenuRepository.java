package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;

import java.util.List;

public interface RestaurantMenuRepository {

    List<RestaurantMenu> findByRestaurantOrderById(Integer restaurantId);
}
