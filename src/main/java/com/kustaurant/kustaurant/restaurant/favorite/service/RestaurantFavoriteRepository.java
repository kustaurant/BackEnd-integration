package com.kustaurant.kustaurant.restaurant.favorite.service;

import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;

import java.util.List;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);

    void delete(RestaurantFavorite restaurantFavorite);

    long countByRestaurantId(Long restaurantId);

}
