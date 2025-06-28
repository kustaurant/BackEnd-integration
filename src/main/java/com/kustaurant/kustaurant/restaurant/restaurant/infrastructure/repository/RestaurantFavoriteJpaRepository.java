package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteJpaRepository extends JpaRepository<RestaurantFavoriteEntity, Integer> {
    Optional<RestaurantFavoriteEntity> findByUserIdAndRestaurant_RestaurantId(Long userId, Integer restaurantId);
    boolean existsByUserIdAndRestaurant_RestaurantId(Long userId, Integer restaurantId);
    List<RestaurantFavoriteEntity> findByUserId(Long userId);

    @Query("""
        SELECT rf FROM RestaurantFavoriteEntity rf
        WHERE rf.userId = :userId
        ORDER BY rf.createdAt DESC""")
    List<RestaurantFavoriteEntity> findSortedFavoritesByUserIdDesc(@Param("userId") Long userId);

}
