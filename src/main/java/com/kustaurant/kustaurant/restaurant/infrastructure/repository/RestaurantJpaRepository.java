package com.kustaurant.kustaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Integer> {
    List<RestaurantEntity> findAll(Specification<RestaurantEntity> spec);
    Page<RestaurantEntity> findAll(Specification<RestaurantEntity> spec, Pageable pageable);
    //
    List<RestaurantEntity> findByStatus(String status);
    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Integer id, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndStatus(String cuisine, String status);
    List<RestaurantEntity> findByRestaurantPositionAndStatus(String position, String status);
    List<RestaurantEntity> findByRestaurantCuisineAndRestaurantPositionAndStatus(String cuisine, String position, String status);

}
