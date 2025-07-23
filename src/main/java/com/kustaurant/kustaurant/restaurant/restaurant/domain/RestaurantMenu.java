package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantMenu {
    private Integer menuId;

    private Integer restaurantId;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;
}
