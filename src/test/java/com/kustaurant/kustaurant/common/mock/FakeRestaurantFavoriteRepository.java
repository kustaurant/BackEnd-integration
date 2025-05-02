package com.kustaurant.kustaurant.common.mock;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeRestaurantFavoriteRepository implements RestaurantFavoriteRepository {
    private final List<RestaurantFavorite> store = new ArrayList<>();


    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return store.stream()
                .filter(fav -> fav.getUser().getId().equals(userId) &&
                        fav.getRestaurant().getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Favorite not found"));

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
