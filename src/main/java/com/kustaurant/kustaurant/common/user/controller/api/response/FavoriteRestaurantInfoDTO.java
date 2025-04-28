package com.kustaurant.kustaurant.common.user.controller.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRestaurantInfoDTO {
    private String restaurantName;
    private Integer restaurantId;
    private String restaurantImgURL;
    private Integer mainTier;
    private String restaurantType;
    private String restaurantPosition;

}
