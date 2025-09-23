package com.kustaurant.mainapp.restaurant.restaurant.domain;

import com.kustaurant.mainapp.restaurant.restaurant.constants.RestaurantConstants;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Restaurant {
    private Long restaurantId;

    private String restaurantName;
    private Cuisine restaurantCuisine;
    private GeoPosition geoPosition;
    private String restaurantType;
    private String restaurantAddress;
    private String restaurantTel;
    private String restaurantUrl;
    private String restaurantImgUrl;
    private String partnershipInfo;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer visitCount;
}