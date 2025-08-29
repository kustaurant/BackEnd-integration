package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;

public interface RestaurantDetailRepository {

    RestaurantDetail getRestaurantDetail(Long restaurantId, Long userId);
}
