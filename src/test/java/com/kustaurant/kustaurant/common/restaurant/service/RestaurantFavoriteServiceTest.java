package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.mock.FakeRestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
        User user = new User();
        user.setUserId(1);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        favoriteService.addFavorite(user, restaurant);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 즐겨찾기_삭제() {
        // Given
        User user = new User();
        user.setUserId(1);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        favoriteService.addFavorite(user, restaurant);
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
        User user = new User();
        user.setUserId(1);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        boolean result = favoriteService.toggleFavorite(user, restaurant);

        // Then
        assertThat(result).isTrue();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 이전에_즐겨찾기가_되어_있었던_경우() {
        // Given
        User user = new User();
        user.setUserId(1);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // 즐겨찾기 추가
        favoriteService.addFavorite(user, restaurant);

        // When
        boolean result = favoriteService.toggleFavorite(user, restaurant);

        // Then
        assertThat(result).isFalse();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getUserId(), restaurant.getRestaurantId()))
                .isFalse();
    }


}