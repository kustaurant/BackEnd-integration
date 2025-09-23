package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.mapper;

import com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.jpa.restaurant.entity.RestaurantMenuEntity;

public class RestaurantMenuMapper {

    public static RestaurantMenu toModel(RestaurantMenuEntity entity) {
        return RestaurantMenu.builder()
                .menuId(entity.getId())
                .menuImgUrl(entity.getMenuImgUrl())
                .menuName(entity.getMenuName())
                .menuPrice(entity.getMenuPrice())
                .naverType(entity.getNaverType())
                .restaurantId(entity.getRestaurantId())
                .build();
    }
}
