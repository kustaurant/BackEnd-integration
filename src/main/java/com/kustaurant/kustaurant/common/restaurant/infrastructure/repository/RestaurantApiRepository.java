package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface RestaurantApiRepository extends JpaRepository<Restaurant,Integer>, JpaSpecificationExecutor<Restaurant> {
    List<Restaurant> findByStatus(String status);
    Optional<Restaurant> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    List<Restaurant> findByStatusAndRestaurantPosition(String status, String restaurantPosition);
    List<Restaurant> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status);
    List<Restaurant> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status,String restaurantPosition);

    Restaurant findByRestaurantId(Integer restaurantId);
}
