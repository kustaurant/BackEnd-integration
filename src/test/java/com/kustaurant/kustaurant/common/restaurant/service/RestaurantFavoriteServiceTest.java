package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.mock.FakeRestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantFavoriteDomain;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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
        UserEntity UserEntity = new UserEntity();
        UserEntity.setUserId(1);
        RestaurantDomain restaurant = RestaurantDomain.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        favoriteService.addFavorite(UserEntity, restaurant);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(UserEntity.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 즐겨찾기_삭제() {
        // Given
        UserEntity UserEntity = new UserEntity();
        UserEntity.setUserId(1);
        RestaurantDomain restaurant = RestaurantDomain.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        favoriteService.addFavorite(UserEntity, restaurant);
        RestaurantFavoriteDomain favorite = favoriteRepository.findByUserIdAndRestaurantId(UserEntity.getUserId(), restaurant.getRestaurantId());

        // When
        favoriteService.deleteFavorite(favorite);

        // Then
        assertThat(favoriteRepository.existsByUserAndRestaurant(UserEntity.getUserId(), restaurant.getRestaurantId()))
                .isFalse();
    }

    @Test
    void 새로_즐겨찾기_추가하는_경우() {
        // Given
        UserEntity UserEntity = new UserEntity();
        UserEntity.setUserId(1);
        RestaurantDomain restaurant = RestaurantDomain.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // When
        boolean result = favoriteService.toggleFavorite(UserEntity, restaurant);

        // Then
        assertThat(result).isTrue();
        assertThat(favoriteRepository.existsByUserAndRestaurant(UserEntity.getUserId(), restaurant.getRestaurantId()))
                .isTrue();
    }

    @Test
    void 이전에_즐겨찾기가_되어_있었던_경우() {
        // Given
        UserEntity UserEntity = new UserEntity();
        UserEntity.setUserId(1);
        RestaurantDomain restaurant = RestaurantDomain.builder()
                .restaurantId(1)
                .restaurantName("곤칼")
                .build();

        // 즐겨찾기 추가
        favoriteService.addFavorite(UserEntity, restaurant);

        // When
        boolean result = favoriteService.toggleFavorite(UserEntity, restaurant);

        // Then
        assertThat(result).isFalse();
        assertThat(favoriteRepository.existsByUserAndRestaurant(UserEntity.getUserId(), restaurant.getRestaurantId()))
                .isFalse();
    }


}