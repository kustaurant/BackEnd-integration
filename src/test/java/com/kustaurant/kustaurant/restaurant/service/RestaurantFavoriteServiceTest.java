package com.kustaurant.kustaurant.restaurant.service;

import com.kustaurant.kustaurant.mock.FakeRestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.application.service.command.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.user.infrastructure.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RestaurantFavoriteServiceTest {
    private RestaurantFavoriteRepository favoriteRepository;
    private RestaurantFavoriteService favoriteService;

    @BeforeEach
    void init() {
        favoriteRepository = new FakeRestaurantFavoriteRepository();
        favoriteService = new RestaurantFavoriteService(favoriteRepository);
    }

    @Test
    void 즐겨찾기_추가() {
        // Given
        Integer userId = 1;
        Integer restaurantId = 2;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName("곤칼")
                .build();

        // When
        favoriteService.addFavorite(userId, restaurantId);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 즐겨찾기_삭제() {
        // Given
        Integer userId = 1;
        Integer restaurantId = 2;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName("곤칼")
                .build();

        favoriteService.addFavorite(userId, restaurantId);
        RestaurantFavorite favorite = favoriteRepository.findByUserIdAndRestaurantId(user.getUserId(), restaurant.getRestaurantId());

        // When
        favoriteService.deleteFavorite(favorite);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isFalse();
    }

    @Test
    void 새로_즐겨찾기_추가하는_경우() {
        // Given
        Integer userId = 1;
        Integer restaurantId = 2;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName("곤칼")
                .build();

        // When
        boolean result = favoriteService.toggleFavorite(userId, restaurantId);

        // Then
        assertThat(result).isTrue();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 이전에_즐겨찾기가_되어_있었던_경우() {
        // Given
        Integer userId = 1;
        Integer restaurantId = 2;
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(restaurantId)
                .restaurantName("곤칼")
                .build();

        // 즐겨찾기 추가
        favoriteService.addFavorite(userId, restaurantId);

        // When
        boolean result = favoriteService.toggleFavorite(userId, restaurantId);

        // Then
        assertThat(result).isFalse();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isFalse();
    }


}