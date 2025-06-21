package com.kustaurant.kustaurant.mock;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class FakeRestaurantFavoriteRepository implements RestaurantFavoriteRepository {
    private final List<RestaurantFavorite> store = new ArrayList<>();


    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return store.stream()
                .filter(fav -> fav.getUser().getId().equals(userId) &&
                        fav.getRestaurant().getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "Favorite not found - UserId:" + userId + ", RestaurantId:" + restaurantId));

    }

    @Override
    public boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId) {
        return store.stream()
                .anyMatch(fav -> fav.getUser().getId().equals(userId) &&
                        fav.getRestaurant().getRestaurantId().equals(restaurantId));
    }

    @Override
    public List<RestaurantFavorite> findByUser(Integer userId) {
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
    public List<RestaurantFavorite> findSortedFavoritesByUserId(Integer userId) {
        return List.of();
    }


    // TODO: need to delete everything below this

    @Override
    public List<RestaurantFavoriteEntity> findAllByUserId(UserEntity user) {
        return List.of();
    }


}
