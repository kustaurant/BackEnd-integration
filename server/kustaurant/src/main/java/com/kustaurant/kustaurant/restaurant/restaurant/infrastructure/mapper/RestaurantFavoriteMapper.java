package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.mapper;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.jpa.restaurant.entity.RestaurantFavoriteEntity;

public class RestaurantFavoriteMapper {

    public static RestaurantFavoriteEntity from(RestaurantFavorite domain) {
        if (domain == null) {
            return null;
        }

        return new RestaurantFavoriteEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getRestaurantId(),
                domain.getStatus()
        );
    }

    public static RestaurantFavorite toModel(RestaurantFavoriteEntity entity) {
        return RestaurantFavorite.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .restaurantId(entity.getRestaurantId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
