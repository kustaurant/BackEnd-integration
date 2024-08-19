package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantFavoriteRepository extends JpaRepository<RestaurantFavorite, Integer> {
    Optional<RestaurantFavorite> findByUserAndRestaurant(User user, Restaurant restaurant);
    List<RestaurantFavorite> findByUser(User user);

    Integer countByRestaurantAndStatus(Restaurant restaurant, String status);
}
