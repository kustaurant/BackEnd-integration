package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository {
    RestaurantFavoriteDomain findByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);
    List<RestaurantFavoriteDomain> findByUser(Integer userId);

    RestaurantFavoriteDomain save(RestaurantFavoriteDomain restaurantFavorite);
    void delete(RestaurantFavoriteDomain restaurantFavorite);

    // TODO: need to delete everything below this
    Optional<RestaurantFavorite> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant);
    List<RestaurantFavorite> findByUser(UserEntity UserEntity);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);

}
