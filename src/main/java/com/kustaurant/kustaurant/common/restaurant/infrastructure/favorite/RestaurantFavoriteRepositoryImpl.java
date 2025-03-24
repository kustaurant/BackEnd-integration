package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;

    @Override
    public RestaurantFavoriteDomain findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return jpaRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId)
                .map(RestaurantFavoriteEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException("요청한 restaurantFavorite이 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
    }

    @Override
    public boolean existsByUserAndRestaurant(Integer userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId);
    }

    @Override
    public List<RestaurantFavoriteDomain> findByUser(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return jpaRepository.findByUser_UserId(userId).stream()
                .map(RestaurantFavoriteEntity::toModel)
                .toList();
    }

    @Override
    @Transactional
    public RestaurantFavoriteDomain save(RestaurantFavoriteDomain restaurantFavorite) {
        return jpaRepository.save(RestaurantFavoriteEntity.from(restaurantFavorite)).toModel();
    }

    @Override
    @Transactional
    public void delete(RestaurantFavoriteDomain restaurantFavorite) {
        jpaRepository.delete(RestaurantFavoriteEntity.from(restaurantFavorite));
    }

    // TODO: need to delete everything below this
    @Override
    public Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(User user, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public List<RestaurantFavoriteEntity> findByUser(User user) {
        return List.of();
    }

    @Override
    public Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status) {
        return 0;
    }
}
