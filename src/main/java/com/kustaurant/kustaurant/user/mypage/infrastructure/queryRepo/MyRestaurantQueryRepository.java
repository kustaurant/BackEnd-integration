package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.MyRestaurantResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyRestaurantQueryRepository {
    private final JPAQueryFactory factory;
    private static final QRestaurantFavoriteEntity favorite = QRestaurantFavoriteEntity.restaurantFavoriteEntity;
    private static final QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;

    public List<MyRestaurantResponse> findActiveFavorites(Long userId) {

        return factory.select(Projections.constructor(
                MyRestaurantResponse.class,
                        restaurant.restaurantName,
                        restaurant.restaurantId,
                        restaurant.restaurantImgUrl,
                        restaurant.mainTier,
                        restaurant.restaurantCuisine,
                        restaurant.restaurantPosition
                ))
                .from(favorite)
                .join(favorite.restaurant, restaurant)
                .where(
                        favorite.userId.eq(userId),
                        favorite.status.eq("ACTIVE")
                )
                .orderBy(restaurant.restaurantName.asc())
                .fetch();
    }
}
