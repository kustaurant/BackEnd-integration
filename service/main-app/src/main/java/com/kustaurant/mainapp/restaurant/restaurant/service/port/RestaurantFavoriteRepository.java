package com.kustaurant.mainapp.restaurant.restaurant.service.port;

import com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantFavorite;

public interface RestaurantFavoriteRepository {

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

    RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);

    void deleteByUserIdAndRestaurantId(Long userId, Long restaurantId);

    long countByRestaurantId(Long restaurantId);

}
