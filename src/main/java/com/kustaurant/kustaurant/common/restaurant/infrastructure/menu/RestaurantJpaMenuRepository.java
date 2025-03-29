package com.kustaurant.kustaurant.common.restaurant.infrastructure.menu;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantJpaMenuRepository extends JpaRepository<RestaurantMenuEntity, Integer> {
    List<RestaurantMenuEntity> findByRestaurant_RestaurantIdOrderByMenuId(Integer restaurantId);
}