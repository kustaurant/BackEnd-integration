package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

public interface RestaurantFavoriteCheckRepository {

    boolean isUserFavorite(Long userId, Integer restaurantId);
}
