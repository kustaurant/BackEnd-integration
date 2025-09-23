package com.kustaurant.mainapp.v1.mypage.dto;

import lombok.Data;


@Data
public class FavoriteRestaurantInfoDTO {
    private String restaurantName;
    private Integer restaurantId;
    private String restaurantImgURL;
    private Integer mainTier;
    private String restaurantType;
    private String restaurantPosition;

}
