package com.kustaurant.kustaurant.common.restaurant.application.service.command.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository {
    RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);
    List<RestaurantFavorite> findByUser(Integer userId);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);
    void delete(RestaurantFavorite restaurantFavorite);

    // TODO: need to delete everything below this
    Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant);
    List<RestaurantFavoriteEntity> findByUser(UserEntity user);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);

}
