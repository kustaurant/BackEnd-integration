package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
        List<RestaurantFavorite> favorites = restaurantFavoriteRepository.findByUser(userId);

        return favorites.stream()
                .map(RestaurantFavorite::getRestaurant)
                .map(restaurantDomain -> RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurantDomain,
                        null,
                        null,
                        null
                )).toList();
    }

    // 즐겨찾기 토글
    @Transactional
    public boolean toggleFavorite(User user, Restaurant restaurant) {
        RestaurantFavorite favorite;
        try {
            // 즐겨찾기 정보 조회
            favorite = restaurantFavoriteRepository.findByUserIdAndRestaurantId(user.getUserId(), restaurant.getRestaurantId());
        } catch (DataNotFoundException e) {
            // 즐겨찾기가 안 되어 있는 경우
            addFavorite(user, restaurant);
            return true;
        }
        // 즐겨찾기가 되어 있던 경우
        deleteFavorite(favorite);
        return false;
    }

    public void addFavorite(User user, Restaurant restaurant) {
        restaurantFavoriteRepository.save(
                RestaurantFavorite.builder()
                .user(user)
                .restaurant(restaurant)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build()
        );
    }

    public void deleteFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantFavoriteRepository.delete(restaurantFavorite);
    }
}
