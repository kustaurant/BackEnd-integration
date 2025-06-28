package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.MyRestaurantResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyFavoriteRestaurantQueryRepository extends Repository<RestaurantFavoriteEntity, Long> {

    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.controller.response.MyRestaurantResponse(
            r.restaurantName,
            r.restaurantId,
            r.restaurantImgUrl,
            r.mainTier,
            r.restaurantCuisine,
            r.restaurantPosition
        )
        from RestaurantFavoriteEntity f
        join f.restaurant r
        where f.userId = :userId
          and f.status = 'ACTIVE'
    """)
    List<MyRestaurantResponse> findActiveFavorites(@Param("userId") Long userId);
}
