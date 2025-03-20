package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantMenuDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.menu.RestaurantMenu;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;

import java.util.List;

public interface RestaurantMenuRepository {

    List<RestaurantMenuDomain> findByRestaurantOrderById(Integer restaurantId);

    // TODO: must delete everying below this
    List<RestaurantMenu> findByRestaurantOrderByMenuId(RestaurantEntity restaurant);
}
