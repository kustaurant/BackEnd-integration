package com.kustaurant.kustaurant.restaurant.query.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;

@Getter
@Schema(description = "restaurant tier entity")
public class RestaurantCoreInfoDto extends RestaurantBaseInfoDto{
    private final Boolean isEvaluated;
    private final Boolean isFavorite;

    public RestaurantCoreInfoDto(RestaurantBaseInfoDto base, boolean evaluated, boolean favorite) {
        super(base);
        this.isEvaluated = evaluated;
        this.isFavorite = favorite;
    }

    @QueryProjection
    public RestaurantCoreInfoDto(
            Long restaurantId, String restaurantName, String restaurantCuisine, String restaurantPosition,
            String restaurantImgUrl, Integer mainTier, Boolean isTempTier, Double longitude, Double latitude,
            String partnershipInfo, Double score, java.util.Set<String> situations, String restaurantType,
            Boolean isEvaluated, Boolean isFavorite
    ) {
        super(restaurantId, restaurantName, restaurantCuisine, restaurantPosition,
                restaurantImgUrl, mainTier, isTempTier, longitude, latitude, partnershipInfo,
                score, situations, restaurantType);
        this.isEvaluated = isEvaluated;
        this.isFavorite = isFavorite;
    }

}
