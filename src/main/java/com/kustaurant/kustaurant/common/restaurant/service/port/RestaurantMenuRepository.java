package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenu;

import java.util.List;

public interface RestaurantMenuRepository {

    List<RestaurantMenu> findByRestaurantOrderById(Integer restaurantId);
}
