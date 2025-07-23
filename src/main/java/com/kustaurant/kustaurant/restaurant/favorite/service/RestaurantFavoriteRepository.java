package com.kustaurant.kustaurant.restaurant.favorite.service;

import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;

import java.util.List;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);

    void delete(RestaurantFavorite restaurantFavorite);

    List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId);

    long countByRestaurantId(Integer restaurantId);

}
