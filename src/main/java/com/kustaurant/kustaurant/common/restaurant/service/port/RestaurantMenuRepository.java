package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenuDomain;

import java.util.List;

public interface RestaurantMenuRepository {

    List<RestaurantMenuDomain> findByRestaurantOrderById(Integer restaurantId);
}
