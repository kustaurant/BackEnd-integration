package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteJpaRepository extends JpaRepository<RestaurantFavoriteEntity, Integer> {
    Optional<RestaurantFavoriteEntity> findByUserIdAndRestaurantId(Long userId, Long restaurantId);

    long countByRestaurantIdAndStatus(Long restaurantId, String status);

    long deleteByUserIdAndRestaurantId(Long userId, Long restaurantId);

    @Query("""
        SELECT rf FROM RestaurantFavoriteEntity rf
        WHERE rf.userId = :userId
        ORDER BY rf.createdAt DESC""")
    List<RestaurantFavoriteEntity> findSortedFavoritesByUserIdDesc(@Param("userId") Long userId);

    boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);

}
