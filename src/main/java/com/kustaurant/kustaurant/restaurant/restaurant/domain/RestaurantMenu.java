package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(of = "menuId")
public class RestaurantMenu {
    private Integer menuId;

    private Integer restaurantId;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;

    @QueryProjection
    public RestaurantMenu(Integer menuId, Integer restaurantId, String menuName, String menuPrice,
            String naverType, String menuImgUrl) {
        this.menuId = menuId;
        this.restaurantId = restaurantId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.naverType = naverType;
        this.menuImgUrl = menuImgUrl;
    }
}
