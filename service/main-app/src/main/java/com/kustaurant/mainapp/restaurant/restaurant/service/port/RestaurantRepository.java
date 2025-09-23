package com.kustaurant.mainapp.restaurant.restaurant.service.port;

import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;

public interface RestaurantRepository {

    Restaurant getByIdAndStatus(Long id, String status);
    void increaseVisitCount(Long restaurantId);
}
