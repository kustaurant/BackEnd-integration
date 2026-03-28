package com.kustaurant.kustaurant.restaurant.restaurant.service.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.restaurant.restaurant.service.dto.QRestaurantDetail is a Querydsl Projection type for RestaurantDetail
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QRestaurantDetail extends ConstructorExpression<RestaurantDetail> {

    private static final long serialVersionUID = -1246781245L;

    public QRestaurantDetail(com.querydsl.core.types.Expression<Long> restaurantId, com.querydsl.core.types.Expression<String> restaurantImgUrl, com.querydsl.core.types.Expression<Integer> mainTier, com.querydsl.core.types.Expression<Boolean> isTempTier, com.querydsl.core.types.Expression<String> restaurantCuisine, com.querydsl.core.types.Expression<String> restaurantPosition, com.querydsl.core.types.Expression<String> restaurantName, com.querydsl.core.types.Expression<String> restaurantAddress, com.querydsl.core.types.Expression<String> naverMapUrl, com.querydsl.core.types.Expression<? extends java.util.Set<String>> situationSet, com.querydsl.core.types.Expression<String> partnershipInfo, com.querydsl.core.types.Expression<Integer> evaluationCount, com.querydsl.core.types.Expression<Double> score, com.querydsl.core.types.Expression<Boolean> isEvaluated, com.querydsl.core.types.Expression<Boolean> isFavorite, com.querydsl.core.types.Expression<Long> favoriteCount, com.querydsl.core.types.Expression<? extends java.util.Set<com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu>> restaurantMenuSet, com.querydsl.core.types.Expression<String> restaurantType, com.querydsl.core.types.Expression<String> restaurantTel, com.querydsl.core.types.Expression<Integer> visitCount, com.querydsl.core.types.Expression<Double> latitude, com.querydsl.core.types.Expression<Double> longitude) {
        super(RestaurantDetail.class, new Class<?>[]{long.class, String.class, int.class, boolean.class, String.class, String.class, String.class, String.class, String.class, java.util.Set.class, String.class, int.class, double.class, boolean.class, boolean.class, long.class, java.util.Set.class, String.class, String.class, int.class, double.class, double.class}, restaurantId, restaurantImgUrl, mainTier, isTempTier, restaurantCuisine, restaurantPosition, restaurantName, restaurantAddress, naverMapUrl, situationSet, partnershipInfo, evaluationCount, score, isEvaluated, isFavorite, favoriteCount, restaurantMenuSet, restaurantType, restaurantTel, visitCount, latitude, longitude);
    }

}

