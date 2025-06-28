package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Long userId, Integer restaurantId) {
        return jpaRepository.findByUserIdAndRestaurant_RestaurantId(userId, restaurantId)
                .map(RestaurantFavoriteEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "요청한 restaurantFavorite이 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
    }

    @Override
    public boolean existsByUserAndRestaurant(Long userId, Integer restaurantId) {
        if (userId == null || restaurantId == null) {
            return false;
        }
        return jpaRepository.existsByUserIdAndRestaurant_RestaurantId(userId, restaurantId);
    }

    @Override
    public List<RestaurantFavorite> findByUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return jpaRepository.findByUserId(userId).stream()
                .map(RestaurantFavoriteEntity::toDomain)
                .toList();
    }


    @Override
    @Transactional
    public RestaurantFavorite save(RestaurantFavorite restaurantFavorite) {
        return jpaRepository.save(
                RestaurantFavoriteEntity.fromDomain(
                        restaurantFavorite,
                        restaurantFavorite.getUserId(),
                        restaurantRepository.getReference(restaurantFavorite.getRestaurantId())
        )).toDomain();
    }

    @Override
    @Transactional
    public void delete(RestaurantFavorite restaurantFavorite) {
        jpaRepository.delete(RestaurantFavoriteEntity.fromDomain(
                restaurantFavorite,
                restaurantFavorite.getUserId(),
                restaurantRepository.getReference(restaurantFavorite.getRestaurantId())
        ));
    }

    @Override
    public List<RestaurantFavorite> findSortedFavoritesByUserId(Long userId) {
        return jpaRepository.findSortedFavoritesByUserIdDesc(userId)
                .stream()
                .map(RestaurantFavorite::from) // Entity → Domain
                .toList();
    }

}
