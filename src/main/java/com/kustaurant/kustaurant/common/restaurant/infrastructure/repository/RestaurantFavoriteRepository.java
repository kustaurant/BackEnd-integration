package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository extends JpaRepository<RestaurantFavorite, Integer> {
    Optional<RestaurantFavorite> findByUserAndRestaurant(User user, Restaurant restaurant);
    List<RestaurantFavorite> findByUser(User user);

    Integer countByRestaurantAndStatus(Restaurant restaurant, String status);
}
