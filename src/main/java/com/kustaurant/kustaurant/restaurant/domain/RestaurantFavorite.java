package com.kustaurant.kustaurant.restaurant.domain;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer favoriteId;
    private Integer userId;
    private Integer restaurantId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantFavorite create(Integer userId, Integer restaurantId) {
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
                .userId(entity.getUser().getUserId())
                .restaurantId(entity.getRestaurant().getRestaurantId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
