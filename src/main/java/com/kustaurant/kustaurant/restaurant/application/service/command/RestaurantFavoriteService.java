package com.kustaurant.kustaurant.restaurant.application.service.command;

import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
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

    // 유저의 즐겨찾기 식당 리스트를 반환
    public List<Integer> getFavoriteRestaurantIdList(Integer userId) {
        List<RestaurantFavorite> favorites = restaurantFavoriteRepository.findByUser(userId);

        return favorites.stream()
                .map(RestaurantFavorite::getRestaurantId)
                .toList();
    }

    // 즐겨찾기 토글
    @Transactional
    public boolean toggleFavorite(Integer userId, Integer restaurantId) {
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

    public void addFavorite(Integer userId, Integer restaurantId) {
        restaurantFavoriteRepository.save(
                RestaurantFavorite.create(userId, restaurantId)
        );
    }

    public void deleteFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantFavoriteRepository.delete(restaurantFavorite);
    }
}
