package com.kustaurant.kustaurant.restaurant.domain;

import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer favoriteId;
    private Long userId;
    Restaurant restaurant;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantFavorite from(RestaurantFavoriteEntity entity) {
        return RestaurantFavorite.builder()
                .favoriteId(entity.getFavoriteId())
                .userId(entity.getUserId())
                .restaurant(Restaurant.from(entity.getRestaurant()))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
