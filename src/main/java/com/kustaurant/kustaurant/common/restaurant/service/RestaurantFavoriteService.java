package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantFavoriteService {
    // repository
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    // 유저의 식당 즐겨찾기 여부를 반환
    public Boolean isUserFavorite(Integer userId, Integer restaurantId) {
        return restaurantFavoriteRepository.existsByUserAndRestaurant(userId, restaurantId);
    }

    // 유저의 즐겨찾기 식당 Tier DTO 리스트를 반환
    public List<RestaurantTierDTO> getFavoriteRestaurantDtoList(Integer userId) {
        List<RestaurantFavoriteDomain> favorites = restaurantFavoriteRepository.findByUser(userId);

        return favorites.stream()
                .map(RestaurantFavoriteDomain::getRestaurant)
                .map(restaurantDomain -> RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurantDomain,
                        null,
                        null,
                        null
                )).toList();
    }

    // 즐겨찾기 토글
    @Transactional
    public boolean toggleFavorite(UserEntity UserEntity, RestaurantDomain restaurant) {
        RestaurantFavoriteDomain favorite;
        try {
            // 즐겨찾기 정보 조회
            favorite = restaurantFavoriteRepository.findByUserIdAndRestaurantId(UserEntity.getUserId(), restaurant.getRestaurantId());
        } catch (DataNotFoundException e) {
            // 즐겨찾기가 안 되어 있는 경우
            addFavorite(UserEntity, restaurant);
            return true;
        }
        // 즐겨찾기가 되어 있던 경우
        deleteFavorite(favorite);
        return false;
    }

    public void addFavorite(UserEntity UserEntity, RestaurantDomain restaurant) {
        restaurantFavoriteRepository.save(
                RestaurantFavoriteDomain.builder()
                .user(UserEntity)
                .restaurant(restaurant)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    public void deleteFavorite(RestaurantFavoriteDomain restaurantFavorite) {
        restaurantFavoriteRepository.delete(restaurantFavorite);
    }
}
