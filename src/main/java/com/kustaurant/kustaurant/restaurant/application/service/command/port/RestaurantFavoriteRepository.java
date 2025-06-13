package com.kustaurant.kustaurant.restaurant.application.service.command.port;

import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;

import java.util.List;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);
    List<RestaurantFavorite> findByUser(Integer userId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);
    void delete(RestaurantFavorite restaurantFavorite);

    List<RestaurantFavorite> findSortedFavoritesByUserId(Integer userId);

    // TODO: need to delete everything below this

    List<RestaurantFavoriteEntity> findAllByUserId(UserEntity user);

}
