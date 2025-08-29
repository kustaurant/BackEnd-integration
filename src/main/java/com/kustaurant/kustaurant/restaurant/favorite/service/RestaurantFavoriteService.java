package com.kustaurant.kustaurant.restaurant.favorite.service;

import com.kustaurant.kustaurant.restaurant.favorite.model.RestaurantFavorite;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantFavoriteService {
    // repository
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;

    public long countByRestaurantId(Long restaurantId) {
        return restaurantFavoriteRepository.countByRestaurantId(restaurantId);
    }

    // 즐겨찾기 토글
    @Transactional
    public boolean toggleFavorite(Long userId, Long restaurantId) {
        RestaurantFavorite favorite;
        try {
            // 즐겨찾기 정보 조회
            favorite = restaurantFavoriteRepository.findByUserIdAndRestaurantId(userId, restaurantId);
        } catch (DataNotFoundException e) {
            // 즐겨찾기가 안 되어 있는 경우
            addFavorite(userId, restaurantId);
            return true;
        }
        // 즐겨찾기가 되어 있던 경우
        deleteFavorite(favorite);
        return false;
    }

    public void addFavorite(Long userId, Long restaurantId) {
        restaurantFavoriteRepository.save(
                RestaurantFavorite.create(userId, restaurantId)
        );
    }

    public void deleteFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantFavoriteRepository.delete(restaurantFavorite);
    }
}
