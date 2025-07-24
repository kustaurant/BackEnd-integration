package com.kustaurant.kustaurant.restaurant.favorite.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository,
        RestaurantFavoriteCheckRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;

    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId) {
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
    public void delete(RestaurantFavorite restaurantFavorite) {
        jpaRepository.delete(RestaurantFavoriteEntity.from(restaurantFavorite));
    }

    @Override
    public List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId) {
        return jpaRepository.findSortedFavoritesByUserIdDesc(userId)
                .stream()
                .map(RestaurantFavoriteEntity::toModel) // Entity → Domain
                .toList();
    }

    @Override
    public long countByRestaurantId(Integer restaurantId) {
        return jpaRepository.countByRestaurantIdAndStatus(restaurantId, "ACTIVE");
    }

    @Override
    public boolean isUserFavorite(Long userId, Integer restaurantId) {
        return jpaRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }
}
