package com.kustaurant.kustaurant.restaurant.infrastructure.repository;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantRepository;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.user.service.port.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RestaurantFavoriteRepositoryImpl implements RestaurantFavoriteRepository {

    private final RestaurantFavoriteJpaRepository jpaRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public RestaurantFavorite findByUserIdAndRestaurantId(Integer userId, Integer restaurantId) {
        return jpaRepository.findByUser_UserIdAndRestaurant_RestaurantId(userId, restaurantId)
                .map(RestaurantFavoriteEntity::toDomain)
                .orElseThrow(() -> new DataNotFoundException(RESTAURANT_FAVORITE_NOT_FOUND, "요청한 restaurantFavorite이 존재하지 않습니다. 요청 정보 - userId: " + userId + ", restaurantId: " + restaurantId));
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
        return jpaRepository.save(RestaurantFavoriteEntity.fromDomain(
                restaurantFavorite,
                userRepository.getReference(restaurantFavorite.getUserId()),
                restaurantRepository.getReference(restaurantFavorite.getRestaurantId())
        )).toDomain();
    }

    @Override
    @Transactional
    public void delete(RestaurantFavorite restaurantFavorite) {
        jpaRepository.delete(RestaurantFavoriteEntity.fromDomain(
                restaurantFavorite,
                userRepository.getReference(restaurantFavorite.getUserId()),
                restaurantRepository.getReference(restaurantFavorite.getRestaurantId())
        ));
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
