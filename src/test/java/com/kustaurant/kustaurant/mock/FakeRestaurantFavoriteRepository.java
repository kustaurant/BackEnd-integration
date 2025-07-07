package com.kustaurant.kustaurant.mock;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeRestaurantFavoriteRepository implements RestaurantFavoriteRepository {
    private final List<RestaurantFavorite> store = new ArrayList<>();


    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId) {
        return store.stream()
                .filter(fav -> fav.getUserId().equals(userId) &&
                        fav.getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "Favorite not found - UserId:" + userId + ", RestaurantId:" + restaurantId));
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        return store.stream()
                .anyMatch(fav -> fav.getUserId().equals(userId) &&
                        fav.getRestaurantId().equals(restaurantId));
    }

    @Override
    public List<RestaurantFavorite> findByUser(Long userId) {
        return List.of();
    }


    @Override
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        store.add(restaurantFavorite);
        return restaurantFavorite;
    }

    @Override
    public void delete(RestaurantFavorite restaurantFavorite) {
        store.remove(restaurantFavorite);
    }

    @Override
    public List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId) {
        return List.of();
    }

}
