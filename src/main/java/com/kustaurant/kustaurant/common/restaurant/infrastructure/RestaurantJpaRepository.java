package com.kustaurant.kustaurant.common.restaurant.infrastructure;

import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Integer> {
    List<RestaurantEntity> findByStatus(String status);
    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Integer id, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndStatus(String cuisine, String status);
    List<RestaurantEntity> findByRestaurantPositionAndStatus(String position, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndRestaurantPositionAndStatus(String cuisine, String position, String status);
}
