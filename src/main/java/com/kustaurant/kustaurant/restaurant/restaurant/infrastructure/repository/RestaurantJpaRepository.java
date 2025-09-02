package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Long> {

    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Long id, String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update RestaurantEntity r set r.visitCount = r.visitCount + 1 where r.restaurantId = :id")
    int incrementViews(@Param("id") long id);
}
