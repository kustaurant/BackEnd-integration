package com.kustaurant.kustaurant.common.restaurant.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantMenu {
    private Integer menuId;

    Restaurant restaurant;
    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;
}
