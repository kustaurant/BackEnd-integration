package com.kustaurant.kustaurant.restaurant.application.service.command.port;

import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;

import java.util.List;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Long userId, Integer restaurantId);
    List<RestaurantFavorite> findByUser(Long userId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);
    void delete(RestaurantFavorite restaurantFavorite);

    List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId);


}
