package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantFavoriteRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RestaurantFavoriteService {
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    public String toggleFavorite(String userTokenId, Integer restaurantId) {
        Optional<User> userOptional = userRepository.findByNaverProviderId(userTokenId);
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Optional<RestaurantFavorite> restaurantFavoriteOptional = restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant);
            if (restaurantFavoriteOptional.isPresent()) { // 즐겨찾기가 되어 있었던 경우
                deleteFavorite(restaurantFavoriteOptional.get());
                return "delete";
            } else { // 즐겨찾기가 되어 있지 않았던 경우
                addFavorite(user, restaurant);
                return "add";
            }
        } else {
            return "fail";
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
        Optional<User> userOptional = userRepository.findByNaverProviderId(userTokenId);
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
}
