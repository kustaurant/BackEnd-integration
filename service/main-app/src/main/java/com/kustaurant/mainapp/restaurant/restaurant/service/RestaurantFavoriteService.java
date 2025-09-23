package com.kustaurant.mainapp.restaurant.restaurant.service;

import com.kustaurant.mainapp.restaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.restaurant.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.mainapp.user.mypage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RestaurantFavoriteService {
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    private final UserStatsService userStatsService;

    public long countByRestaurantId(Long restaurantId) {
        return restaurantFavoriteRepository.countByRestaurantId(restaurantId);
    }

    // 즐겨찾기 추가
    @Transactional
    public boolean addFavorite(Long userId, Long restaurantId) {
        if (!restaurantFavoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            // 즐겨찾기가 안 되어 있는 경우 추가
            restaurantFavoriteRepository.save(RestaurantFavorite.create(userId, restaurantId));
            userStatsService.incFavoriteRestaurant(userId);
        }
        return true;
    }

    // 즐겨찾기 제거
    @Transactional
    public boolean deleteFavorite(Long userId, Long restaurantId) {
        if (restaurantFavoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId)) {
            // 즐겨찾기 되어 있는 경우 제거
            restaurantFavoriteRepository.deleteByUserIdAndRestaurantId(userId, restaurantId);
            userStatsService.decFavoriteRestaurant(userId);
        }
        return false;
    }
}
