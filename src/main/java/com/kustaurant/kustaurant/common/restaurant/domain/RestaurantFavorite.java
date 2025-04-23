package com.kustaurant.kustaurant.common.restaurant.domain;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer favoriteId;
    User user;
    Restaurant restaurant;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantFavorite from(RestaurantFavoriteEntity entity) {
        return RestaurantFavorite.builder()
                .favoriteId(entity.getFavoriteId())
                .user(User.from(entity.getUser()))
                .restaurant(Restaurant.from(entity.getRestaurant()))
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
