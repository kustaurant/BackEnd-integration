package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.kustaurant.kustaurant.restaurant.restaurant.domain.QRestaurantMenu is a Querydsl Projection type for RestaurantMenu
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QRestaurantMenu extends ConstructorExpression<RestaurantMenu> {

    private static final long serialVersionUID = 1318924409L;

    public QRestaurantMenu(com.querydsl.core.types.Expression<Integer> menuId, com.querydsl.core.types.Expression<Long> restaurantId, com.querydsl.core.types.Expression<String> menuName, com.querydsl.core.types.Expression<String> menuPrice, com.querydsl.core.types.Expression<String> naverType, com.querydsl.core.types.Expression<String> menuImgUrl) {
        super(RestaurantMenu.class, new Class<?>[]{int.class, long.class, String.class, String.class, String.class, String.class}, menuId, restaurantId, menuName, menuPrice, naverType, menuImgUrl);
    }

}

