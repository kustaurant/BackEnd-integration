package com.kustaurant.kustaurant.common.restaurant.service;

import com.kustaurant.kustaurant.api.restaurant.service.RestaurantApiService;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavorite;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.RestaurantFavoriteRepository;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.RestaurantRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantFavoriteService {
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantApiService restaurantApiService;

    public boolean toggleFavorite(String userTokenId, Integer restaurantId) {
        Optional<User> userOptional = userRepository.findByProviderId(userTokenId);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");

        if (restaurantOptional.isEmpty()) {
            throw new OptionalNotExistException(restaurantId + " 식당을 찾지 못했습니다.");
        }
        Restaurant restaurant = restaurantOptional.get();

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<RestaurantFavorite> restaurantFavoriteOptional = restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant);
            if (restaurantFavoriteOptional.isPresent()) { // 즐겨찾기가 되어 있었던 경우
                deleteFavorite(restaurantFavoriteOptional.get());
                return false;
            } else { // 즐겨찾기가 되어 있지 않았던 경우
                addFavorite(user, restaurant);
                return true;
            }
        } else {
            throw new OptionalNotExistException(userTokenId + " 유저를 찾지 못했습니다.");
        }
    }

    public void addFavorite(User user, Restaurant restaurant) {
        RestaurantFavorite restaurantFavorite = new RestaurantFavorite();
        restaurantFavorite.setUser(user);
        restaurantFavorite.setRestaurant(restaurant);
        restaurantFavorite.setStatus("ACTIVE");
        restaurantFavorite.setCreatedAt(LocalDateTime.now());
        restaurantFavoriteRepository.save(restaurantFavorite);
    }
    public void deleteFavorite(RestaurantFavorite restaurantFavorite) {
        restaurantFavoriteRepository.delete(restaurantFavorite);
    }

    public boolean isFavoriteExist(String userTokenId, Integer restaurantId) {
        Optional<User> userOptional = userRepository.findByProviderId(userTokenId);
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<RestaurantFavorite> restaurantFavoriteOptional = restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant);
            if (restaurantFavoriteOptional.isPresent()) { // 즐겨찾기가 되어 있었던 경우
                return true;
            } else { // 즐겨찾기가 되어 있지 않았던 경우
                return false;
            }
        } else {
            return false;
        }
    }

    public Integer getFavoriteCountByRestaurant(Restaurant restaurant) {
        return restaurantFavoriteRepository.countByRestaurantAndStatus(restaurant, "ACTIVE");
    }

    // 유저의 즐겨찾기 식당 Tier DTO 리스트를 반환
    public List<RestaurantTierDTO> getFavoriteRestaurantDtoList(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return user.getRestaurantFavoriteList().stream()
                .map(restaurantFavorite -> RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurantFavorite.getRestaurant(),
                        null,
                        restaurantApiService.isEvaluated(restaurantFavorite.getRestaurant(), user),
                        restaurantApiService.isFavorite(restaurantFavorite.getRestaurant(), user)
                )).collect(Collectors.toList());
    }
}
