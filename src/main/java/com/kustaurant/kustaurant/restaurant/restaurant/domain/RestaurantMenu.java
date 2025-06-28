package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantMenu {
    private Integer menuId;

    @JsonIgnore
    Restaurant restaurant;

    private String menuName;
    private String menuPrice;
    private String naverType;
    private String menuImgUrl;
}
