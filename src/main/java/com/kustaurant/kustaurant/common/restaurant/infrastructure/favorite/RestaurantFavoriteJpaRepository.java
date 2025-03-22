package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteJpaRepository extends JpaRepository<RestaurantFavoriteEntity, Integer> {
    boolean existsByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);

    // TODO: need to delete everything below this

    Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(User user, RestaurantEntity restaurant);
    List<RestaurantFavoriteEntity> findByUser(User user);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);
}
