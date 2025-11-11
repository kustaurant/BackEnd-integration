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
public class RestaurantBaseInfoDto {
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
    protected String partnershipInfo;
    protected Double restaurantScore;
    @JsonIgnore protected List<String> situations;
    @JsonIgnore protected String restaurantType;

    @QueryProjection
    public RestaurantBaseInfoDto(
            Long restaurantId, String restaurantName, String restaurantCuisine, String restaurantPosition,
            String restaurantImgUrl, Integer mainTier, Boolean isTempTier, Double longitude, Double latitude,
            String partnershipInfo, Double score, Set<String> situations, String restaurantType
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
        this.partnershipInfo = partnershipInfo;
        this.restaurantScore = score;
        this.situations = (situations == null) ? List.of() : List.copyOf(situations);
        this.restaurantType = restaurantType;
    }

    protected RestaurantBaseInfoDto(RestaurantBaseInfoDto src) {
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
        this.partnershipInfo = src.partnershipInfo;
        this.restaurantScore = src.restaurantScore;
        this.situations = src.situations;
        this.restaurantType = src.restaurantType;
    }

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
