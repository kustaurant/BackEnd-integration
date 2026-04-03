package com.kustaurant.kustaurant.restaurant.query.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.util.List;
import java.util.Set;

import static com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants.positionPostprocessing;
import static com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants.restaurantImgUrlPostProcessing;

@Getter
public class RestaurantBaseInfoDtoV2 {
    protected Long restaurantId;
    protected Integer restaurantRanking;
    protected String restaurantName;
    protected String restaurantCuisine;
    protected String restaurantPosition;
    protected String restaurantImgUrl;
    protected Integer mainTier;
    protected Boolean isTempTier;
    protected Double longitude;
    protected Double latitude;
    protected Set<String> partnerships;
    protected Double restaurantScore;
    @JsonIgnore protected List<String> situations;
    @JsonIgnore protected String restaurantType;

    @QueryProjection
    public RestaurantBaseInfoDtoV2(
            Long restaurantId, String restaurantName, String restaurantCuisine, String restaurantPosition,
            String restaurantImgUrl, Integer mainTier, Boolean isTempTier, Double longitude, Double latitude,
            Set<String> partnerships, Double score, Set<String> situations, String restaurantType
    ) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantCuisine = restaurantCuisine;
        this.restaurantPosition = positionPostprocessing(restaurantPosition);
        this.restaurantImgUrl = restaurantImgUrlPostProcessing(restaurantImgUrl);
        this.mainTier = mainTier;
        this.isTempTier = isTempTier;
        this.longitude = longitude;
        this.latitude = latitude;
        this.partnerships = partnerships;
        this.restaurantScore = score;
        this.situations = (situations == null) ? List.of() : List.copyOf(situations);
        this.restaurantType = restaurantType;
    }

    protected RestaurantBaseInfoDtoV2(RestaurantBaseInfoDtoV2 src) {
        this.restaurantId = src.restaurantId;
        this.restaurantRanking = src.restaurantRanking;
        this.restaurantName = src.restaurantName;
        this.restaurantCuisine = src.restaurantCuisine;
        this.restaurantPosition = src.restaurantPosition;
        this.restaurantImgUrl = src.restaurantImgUrl;
        this.mainTier = src.mainTier;
        this.isTempTier = src.isTempTier;
        this.longitude = src.longitude;
        this.latitude = src.latitude;
        this.partnerships = src.partnerships;
        this.restaurantScore = src.restaurantScore;
        this.situations = src.situations;
        this.restaurantType = src.restaurantType;
    }

    // 프론트에서 사용중인 2개의 메서드 지우지 말것
    public String getTierImgUrl() {
        if (mainTier == null || mainTier <= 0) return null;
        return RestaurantConstants.getTierImgUrl(mainTier);
    }

    public String getCuisineImgUrl() {
        if (restaurantCuisine == null || restaurantCuisine.isBlank()) return null;
        return RestaurantConstants.getCuisineImgUrl(restaurantCuisine);
    }

    public void assembleRanking(int ranking) {
        if (mainTier != null && mainTier > 0) {
            this.restaurantRanking = ranking;
        }
    }
}
