package com.kustaurant.mainapp.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;

    @Override
    public boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return jpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return jpaRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .map(RestaurantFavoriteEntity::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "요청한 restaurantFavorite이 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
    }

    @Override
    @Transactional
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        return jpaRepository.save(
                RestaurantFavoriteEntity.from(restaurantFavorite)).toModel();
    }

    @Override
    @Transactional
    public void deleteByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        jpaRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public long countByRestaurantId(Long restaurantId) {
        return jpaRepository.countByRestaurantIdAndStatus(restaurantId, "ACTIVE");
    }
}
