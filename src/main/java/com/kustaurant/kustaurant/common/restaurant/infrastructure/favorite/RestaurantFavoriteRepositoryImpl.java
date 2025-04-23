package com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
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

    @Override
    public List<RestaurantFavorite> findSortedFavoritesByUserId(Integer userId) {
        return jpaRepository.findSortedFavoritesByUserIdDesc(userId)
                .stream()
                .map(RestaurantFavorite::from) // Entity → Domain
                .toList();
    }

    // TODO: need to delete everything below this

    @Override
    public List<RestaurantFavoriteEntity> findAllByUserId(UserEntity user) {
        return List.of();
    }

}
