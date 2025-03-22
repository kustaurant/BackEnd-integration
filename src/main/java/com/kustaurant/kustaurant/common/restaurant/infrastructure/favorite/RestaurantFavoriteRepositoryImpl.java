package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;

    @Override
    public boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId);
    }

    // TODO: need to delete everything below this
    @Override
    public Optional<RestaurantFavorite> findByUserAndRestaurant(User user, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public List<RestaurantFavorite> findByUser(User user) {
        return List.of();
    }

    @Override
    public Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status) {
        return 0;
    }

    @Override
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        return null;
    }

    @Override
    public RestaurantFavorite delete(RestaurantFavorite restaurantFavorite) {
        return null;
    }
}
