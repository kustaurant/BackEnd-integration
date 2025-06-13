package com.kustaurant.kustaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantJpaMenuRepository extends JpaRepository<RestaurantMenuEntity, Integer> {
    List<RestaurantMenuEntity> findByRestaurant_RestaurantIdOrderByMenuId(Integer restaurantId);
}