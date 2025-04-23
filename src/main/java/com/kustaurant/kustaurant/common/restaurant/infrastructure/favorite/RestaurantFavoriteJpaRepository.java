package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteJpaRepository extends JpaRepository<RestaurantFavoriteEntity, Integer> {
    Optional<RestaurantFavoriteEntity> findByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    boolean existsByUser_UserIdAndRestaurant_RestaurantId(Integer userId, Integer restaurantId);
    List<RestaurantFavoriteEntity> findByUser_UserId(Integer userId);

    @Query("""
        SELECT rf FROM RestaurantFavoriteEntity rf
        WHERE rf.user.id = :userId
        ORDER BY rf.createdAt DESC""")
    List<RestaurantFavoriteEntity> findSortedFavoritesByUserIdDesc(@Param("userId") Integer userId);

}
