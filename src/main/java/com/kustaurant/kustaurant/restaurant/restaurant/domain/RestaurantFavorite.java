package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer favoriteId;
    private Long userId;
    private Integer restaurantId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantFavorite create(Long userId, Integer restaurantId) {
        return RestaurantFavorite.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static RestaurantFavorite from(RestaurantFavoriteEntity entity) {
        return RestaurantFavorite.builder()
                .favoriteId(entity.getFavoriteId())
                .userId(entity.getUserId())
                .restaurantId(entity.getRestaurant().getRestaurantId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
