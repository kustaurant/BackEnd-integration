package com.kustaurant.kustaurant.common.mock;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeRestaurantFavoriteRepository implements RestaurantFavoriteRepository {
    private final List<RestaurantFavoriteDomain> store = new ArrayList<>();


    @Override
    public RestaurantFavoriteDomain findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return store.stream()
                .filter(fav -> fav.getUser().getUserId().equals(userId) &&
                        fav.getRestaurant().getRestaurantId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Favorite not found"));

    }

    @Override
    public boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId) {
        return store.stream()
                .anyMatch(fav -> fav.getUser().getUserId().equals(userId) &&
                        fav.getRestaurant().getRestaurantId().equals(restaurantId));
    }

    @Override
    public List<RestaurantFavoriteDomain> findByUser(Integer userId) {
        List<RestaurantFavoriteDomain> favorites = new ArrayList<>();
        for (RestaurantFavoriteDomain fav : store) {
            if (fav.getUser().getUserId().equals(userId)) {
                favorites.add(fav);
            }
        }
        return favorites;
    }

    @Override
    public RestaurantFavoriteDomain save(RestaurantFavoriteDomain restaurantFavorite) {
        store.add(restaurantFavorite);
        return restaurantFavorite;
    }

    @Override
    public void delete(RestaurantFavoriteDomain restaurantFavorite) {
        store.remove(restaurantFavorite);
    }


    // TODO: need to delete everything below this
    @Override
    public Optional<RestaurantFavorite> findByUserAndRestaurant(UserEntity UserEntity, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public List<RestaurantFavorite> findByUser(UserEntity UserEntity) {
        return List.of();
    }

    @Override
    public Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status) {
        return 0;
    }
}
