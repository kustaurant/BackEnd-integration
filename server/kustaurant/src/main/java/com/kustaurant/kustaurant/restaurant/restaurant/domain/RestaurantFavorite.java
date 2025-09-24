package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RestaurantFavorite {

    private Integer id;
    private Long userId;
    private Long restaurantId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantFavorite create(Long userId, Long restaurantId) {
        return RestaurantFavorite.builder()
                .userId(userId)
                .restaurantId(restaurantId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
