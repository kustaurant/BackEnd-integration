package com.kustaurant.mainapp.v1.restaurant.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestaurantMenu {
    private Integer menuId;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;

    public static RestaurantMenu fromV2(
            com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantMenu v2) {
        return new RestaurantMenu(
                v2.getMenuId(),
                v2.getMenuName(),
                v2.getMenuPrice(),
                v2.getNaverType(),
                v2.getMenuImgUrl()
        );
    }
}