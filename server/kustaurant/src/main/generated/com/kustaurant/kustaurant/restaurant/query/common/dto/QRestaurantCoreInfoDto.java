package com.kustaurant.kustaurant.restaurant.query.common.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.restaurant.query.common.dto.QRestaurantCoreInfoDto is a Querydsl Projection type for RestaurantCoreInfoDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QRestaurantCoreInfoDto extends ConstructorExpression<RestaurantCoreInfoDto> {

    private static final long serialVersionUID = 1214889485L;

    public QRestaurantCoreInfoDto(com.querydsl.core.types.Expression<Long> restaurantId, com.querydsl.core.types.Expression<String> restaurantName, com.querydsl.core.types.Expression<String> restaurantCuisine, com.querydsl.core.types.Expression<String> restaurantPosition, com.querydsl.core.types.Expression<String> restaurantImgUrl, com.querydsl.core.types.Expression<Integer> mainTier, com.querydsl.core.types.Expression<Boolean> isTempTier, com.querydsl.core.types.Expression<Double> longitude, com.querydsl.core.types.Expression<Double> latitude, com.querydsl.core.types.Expression<String> partnershipInfo, com.querydsl.core.types.Expression<Double> score, com.querydsl.core.types.Expression<? extends java.util.Set<String>> situations, com.querydsl.core.types.Expression<String> restaurantType, com.querydsl.core.types.Expression<Boolean> isEvaluated, com.querydsl.core.types.Expression<Boolean> isFavorite) {
        super(RestaurantCoreInfoDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, String.class, int.class, boolean.class, double.class, double.class, String.class, double.class, java.util.Set.class, String.class, boolean.class, boolean.class}, restaurantId, restaurantName, restaurantCuisine, restaurantPosition, restaurantImgUrl, mainTier, isTempTier, longitude, latitude, partnershipInfo, score, situations, restaurantType, isEvaluated, isFavorite);
    }

}

