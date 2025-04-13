package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteJpaRepository extends JpaRepository<RestaurantFavoriteEntity, Integer> {
    Optional<RestaurantFavoriteEntity> findByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    List<RestaurantFavoriteEntity> findByUser_UserId(Integer userId);

    // TODO: need to delete everything below this

    Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant);
    List<RestaurantFavoriteEntity> findByUser(UserEntity UserEntity);

    Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status);
}
