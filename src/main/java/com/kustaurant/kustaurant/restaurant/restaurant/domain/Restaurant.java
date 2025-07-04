package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class Restaurant {
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

    public String getCuisineImgUrl(String cuisine) {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/cuisine-icon/" + restaurantCuisine.replaceAll("/", "") + ".svg";
    }

    public static Restaurant from(RestaurantEntity entity) {
        return Restaurant.builder()
                .restaurantId(entity.getRestaurantId())
                .restaurantName(entity.getRestaurantName())
                .restaurantType(entity.getRestaurantType())
                .restaurantPosition(entity.getRestaurantPosition())
                .restaurantAddress(entity.getRestaurantAddress())
                .restaurantTel(entity.getRestaurantTel())
                .restaurantUrl(entity.getRestaurantUrl())
                .restaurantImgUrl(entity.getRestaurantImgUrl())
                .restaurantCuisine(entity.getRestaurantCuisine())
                .restaurantLatitude(entity.getRestaurantLatitude())
                .restaurantLongitude(entity.getRestaurantLongitude())
                .partnershipInfo(entity.getPartnershipInfo())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .restaurantVisitCount(entity.getRestaurantVisitCount())
                .visitCount(entity.getVisitCount())
                .restaurantEvaluationCount(entity.getRestaurantEvaluationCount())
                .restaurantScoreSum(entity.getRestaurantScoreSum())
                .mainTier(entity.getMainTier())
                .favoriteCount(entity.getRestaurantFavorite().size())
                .build();
    }

    public void afterEvaluationCreated(Double score) {
        this.restaurantScoreSum += score;
        this.restaurantEvaluationCount++;
    }

    public void afterReEvaluated(Double preScore, Double postScore) {
        this.restaurantScoreSum += (postScore - preScore);
    }

    public void changeTier(int mainTier) {
        this.mainTier = mainTier;
    }

    public double getAvgScore() {
        if (restaurantEvaluationCount == 0) {
            return 0.0;
        }
        return restaurantScoreSum / restaurantEvaluationCount;
    }
}