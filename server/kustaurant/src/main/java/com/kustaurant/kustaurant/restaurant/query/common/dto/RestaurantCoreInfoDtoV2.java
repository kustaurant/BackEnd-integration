package com.kustaurant.kustaurant.restaurant.query.common.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Set;

@Getter
@Hidden
public class RestaurantCoreInfoDtoV2 extends RestaurantBaseInfoDtoV2{
    private final Boolean isEvaluated;
    private final Boolean isFavorite;

    public RestaurantCoreInfoDtoV2(RestaurantBaseInfoDtoV2 base, boolean evaluated, boolean favorite) {
        super(base);
        this.isEvaluated = evaluated;
        this.isFavorite = favorite;
    }

    @QueryProjection
    public RestaurantCoreInfoDtoV2(
            Long restaurantId, String restaurantName, String restaurantCuisine, String restaurantPosition,
            String restaurantImgUrl, Integer mainTier, Boolean isTempTier, Double longitude, Double latitude,
            Set<String> partnerships, Double score, Set<String> situations, String restaurantType,
            Boolean isEvaluated, Boolean isFavorite
    ) {
        super(restaurantId, restaurantName, restaurantCuisine, restaurantPosition,
                restaurantImgUrl, mainTier, isTempTier, longitude, latitude, partnerships,
                score, situations, restaurantType);
        this.isEvaluated = isEvaluated;
        this.isFavorite = isFavorite;
    }

}
