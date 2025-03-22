package com.kustaurant.kustaurant.common.restaurant.service.port;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository {

    boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId);

    // TODO: need to delete everything below this
    Optional<RestaurantFavorite> findByUserAndRestaurant(User user, RestaurantEntity restaurant);
    List<RestaurantFavorite> findByUser(User user);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);

    RestaurantFavorite save(RestaurantFavorite restaurantFavorite);
    RestaurantFavorite delete(RestaurantFavorite restaurantFavorite);
}
