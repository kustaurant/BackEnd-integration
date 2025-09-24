package com.kustaurant.kustaurant.mock.restaurant;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeRestaurantFavoriteRepository implements RestaurantFavoriteRepository {
    private final List<RestaurantFavorite> store = new ArrayList<>();


    @Override
    public boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return false;
    }

    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return store.stream()
                .filter(fav -> fav.getUserId().equals(userId) &&
                        fav.getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "Favorite not found - UserId:" + userId + ", RestaurantId:" + restaurantId));
    }


    @Override
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        store.add(restaurantFavorite);
        return restaurantFavorite;
    }

    @Override
    public void deleteByUserIdAndRestaurantId(Long userId, Long restaurantId) {

    }

    @Override
    public long countByRestaurantId(Long restaurantId) {
        return 0;
    }

}
