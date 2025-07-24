package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    Restaurant getById(Integer id);

    Restaurant getByIdAndStatus(Integer id, String status);

    void updateStatistics(Restaurant restaurant);
}
