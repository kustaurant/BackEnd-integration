package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

public interface RestaurantDetailRepository {

    RestaurantDetail getRestaurantDetail(Integer restaurantId, Long userId);
}
