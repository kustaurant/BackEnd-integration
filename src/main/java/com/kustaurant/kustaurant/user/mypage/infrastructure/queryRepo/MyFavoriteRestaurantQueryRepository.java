package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.restaurant.favorite.infrastructure.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyFavoriteRestaurantQueryRepository extends Repository<RestaurantFavoriteEntity, Long> {

    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.controller.response.api.MyRestaurantResponse(
            r.restaurantName,
            r.restaurantId,
            r.restaurantImgUrl,
            r.mainTier,
            r.restaurantCuisine,
            r.restaurantPosition
        )
        from RestaurantFavoriteEntity f
        join RestaurantEntity r on f.restaurantId = r.restaurantId
        where f.userId = :userId
          and f.status = 'ACTIVE'
    """)
    List<MyRestaurantResponse> findActiveFavorites(@Param("userId") Long userId);
}
