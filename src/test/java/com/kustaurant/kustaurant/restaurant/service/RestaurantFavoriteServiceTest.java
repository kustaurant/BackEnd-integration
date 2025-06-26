package com.kustaurant.kustaurant.restaurant.service;

import com.kustaurant.kustaurant.mock.FakeRestaurantFavoriteRepository;
import com.kustaurant.kustaurant.restaurant.application.service.command.RestaurantFavoriteService;
import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.domain.RestaurantFavorite;
import com.kustaurant.kustaurant.restaurant.application.service.command.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
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
        UserEntity user = new UserEntity();
        user.setId(1L);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        favoriteService.addFavorite(user.getId(), restaurant);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 즐겨찾기_삭제() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        favoriteService.addFavorite(user.getId(), restaurant);
        RestaurantFavorite favorite = favoriteRepository.findByUserIdAndRestaurantId(user.getId(), restaurant.getRestaurantId());

        // When
        favoriteService.deleteFavorite(favorite);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getId(), restaurant.getRestaurantId()))
                .isFalse();
    }

    @Test
    void 새로_즐겨찾기_추가하는_경우() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        boolean result = favoriteService.toggleFavorite(user.getId(), restaurant);

        // Then
        assertThat(result).isTrue();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 이전에_즐겨찾기가_되어_있었던_경우() {
        // Given
        UserEntity user = new UserEntity();
        user.setId(1L);
        Restaurant restaurant = Restaurant.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // 즐겨찾기 추가
        favoriteService.addFavorite(user.getId(), restaurant);

        // When
        boolean result = favoriteService.toggleFavorite(user.getId(), restaurant);

        // Then
        assertThat(result).isFalse();
        assertThat(favoriteRepository.existsByUserAndRestaurant(user.getId(), restaurant.getRestaurantId()))
                .isFalse();
    }


}