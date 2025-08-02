package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;
import static com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity.restaurantEntity;

import com.kustaurant.kustaurant.restaurant.favorite.infrastructure.QRestaurantFavoriteEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
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
                        ratingEntity.tier,
                        restaurant.restaurantCuisine,
                        restaurant.restaurantPosition
                ))
                .from(favorite)
                .join(restaurant).on(favorite.restaurantId.eq(restaurant.restaurantId))
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurantEntity.restaurantId))
                .where(
                        favorite.userId.eq(userId),
                        favorite.status.eq("ACTIVE")
                )
                .orderBy(favorite.createdAt.desc())
                .fetch();
    }
}
