package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;

public interface RestaurantRepository {

    Restaurant getByIdAndStatus(Long id, String status);
    void increaseVisitCount(Long restaurantId);
}
