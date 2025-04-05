package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.discovery.domain.RestaurantTierDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantApiService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantEntity findRestaurantById(Integer restaurantId) {
        Optional<RestaurantEntity> restaurantOptional = restaurantRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");
        if (restaurantOptional.isEmpty()) {
            throw new OptionalNotExistException(restaurantId + " 식당이 없습니다.");
        }
        return restaurantOptional.get();
    }

    @Transactional
    public void saveRestaurant(RestaurantEntity restaurant) {
        restaurantRepository.save(restaurant);
    }
}
