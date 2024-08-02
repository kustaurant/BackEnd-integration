package com.kustaurant.restauranttier.tab5_mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteRestaurantInfoDTO {
    private String restaurantName;
    private String restaurantImgURL;
    private Integer mainTier;
    private String restaurantType;
    private String restaurantPosition;

}
