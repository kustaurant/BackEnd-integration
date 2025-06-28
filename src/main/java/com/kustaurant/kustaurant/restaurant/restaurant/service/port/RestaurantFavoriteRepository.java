package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;

import java.util.List;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Long userId, Integer restaurantId);
    List<RestaurantFavorite> findByUser(Long userId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);
    void delete(RestaurantFavorite restaurantFavorite);

    List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId);


}
