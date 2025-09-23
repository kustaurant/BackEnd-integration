package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import static com.kustaurant.kustaurant.rating.infrastructure.jpa.entity.QRatingEntity.ratingEntity;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.QRestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyRestaurantQueryRepository {
    private final JPAQueryFactory factory;
    private static final QRestaurantFavoriteEntity favorite = QRestaurantFavoriteEntity.restaurantFavoriteEntity;
    private static final QRestaurantEntity restaurant = QRestaurantEntity.restaurantEntity;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MyRestaurantResponse> findMyFavoritesRestaurants(Long userId) {

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
                .leftJoin(ratingEntity).on(ratingEntity.restaurantId.eq(restaurant.restaurantId))
                .where(
                        favorite.userId.eq(userId),
                        favorite.status.eq("ACTIVE")
                )
                .orderBy(favorite.createdAt.desc())
                .fetch();
    }
}
