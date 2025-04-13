package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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
    public RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return jpaRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId)
                .map(RestaurantFavoriteEntity::toDomain)
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
    public List<RestaurantFavorite> findByUser(Integer userId) {
        if (userId == null) {
            return List.of();
        }
        return jpaRepository.findByUser_UserId(userId).stream()
                .map(RestaurantFavoriteEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        return jpaRepository.save(RestaurantFavoriteEntity.fromDomain(restaurantFavorite)).toDomain();
    }

    @Override
    @Transactional
    public void delete(RestaurantFavorite restaurantFavorite) {
        jpaRepository.delete(RestaurantFavoriteEntity.fromDomain(restaurantFavorite));
    }

    // TODO: need to delete everything below this
    @Override
    public Optional<RestaurantFavoriteEntity> findByUserAndRestaurant(UserEntity user, RestaurantEntity restaurant) {
        return Optional.empty();
    }

    @Override
    public List<RestaurantFavoriteEntity> findByUser(UserEntity user) {
        return List.of();
    }

    @Override
    public Integer countByRestaurantAndStatus(RestaurantEntity restaurant, String status) {
        return 0;
    }
}
