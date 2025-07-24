package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.kustaurant.kustaurant.restaurant.restaurant.service.constants.RestaurantConstants;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Restaurant {
    private Integer restaurantId;

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
    private Integer restaurantEvaluationCount;
    private Double restaurantScoreSum;

    private Tier mainTier;

    public String getCuisineImgUrl() {
        return RestaurantConstants.getCuisineImgUrl(restaurantCuisine.getValue());
    }

    public void afterEvaluationCreated(Double score) {
        this.restaurantScoreSum += score;
        this.restaurantEvaluationCount++;
    }

    public void afterReEvaluated(Double preScore, Double postScore) {
        this.restaurantScoreSum += (postScore - preScore);
    }

    public void changeTier(int mainTier) {
        this.mainTier = Tier.find(mainTier);
    }

    public double getAvgScore() {
        if (restaurantEvaluationCount == 0) {
            return 0.0;
        }
        return restaurantScoreSum / restaurantEvaluationCount;
    }
}