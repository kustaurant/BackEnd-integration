package com.kustaurant.kustaurant.common.restaurant.domain;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer favoriteId;
    UserEntity user;
    Restaurant restaurant;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
