package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantJpaMenuRepository extends JpaRepository<RestaurantMenuEntity, Integer> {
    List<RestaurantMenuEntity> findByRestaurantIdOrderById(Integer restaurantId);
}