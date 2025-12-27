package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.mapper.RestaurantFavoriteMapper;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
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
                .map(RestaurantFavoriteMapper::toModel)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "요청한 restaurantFavorite이 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
    }

    @Override
    @Transactional
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        return RestaurantFavoriteMapper.toModel(
                jpaRepository.save(RestaurantFavoriteMapper.from(restaurantFavorite)));
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
