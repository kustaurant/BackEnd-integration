package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository {
    RestaurantFavoriteDomain findByUserIdAndRestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);
    List<RestaurantFavoriteDomain> findByUser(Integer userId);

    RestaurantFavoriteDomain save(RestaurantFavoriteDomain restaurantFavorite);
    void delete(RestaurantFavoriteDomain restaurantFavorite);

    // TODO: need to delete everything below this
    Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(User user, RestaurantEntity restaurant);
    List<RestaurantFavoriteEntity> findByUser(User user);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);

}
