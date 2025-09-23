package com.kustaurant.mainapp.restaurant.restaurant.service.port;

import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {

    Restaurant getByIdAndStatus(Long id, String status);
    void increaseVisitCount(Long restaurantId);
}
