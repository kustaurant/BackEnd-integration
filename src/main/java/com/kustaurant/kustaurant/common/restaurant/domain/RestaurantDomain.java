package com.kustaurant.kustaurant.common.restaurant.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class RestaurantDomain {
    private Integer restaurantId;

    private String restaurantName;
    private String restaurantType;
    private String restaurantPosition;
    private String restaurantAddress;
    private String restaurantTel;
    private String restaurantUrl;
    private String restaurantImgUrl;
    private String restaurantCuisine;
    private String restaurantLatitude;
    private String restaurantLongitude;
    private String partnershipInfo;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer restaurantVisitCount = 0;
    private Integer visitCount = 0;
    private Integer restaurantEvaluationCount = 0;
    private Double restaurantScoreSum = 0d;
    private Integer mainTier = -1;

    private List<String> situations = new ArrayList<>();
    private Integer favoriteCount = 0;
}