package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantMenu;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Integer> {
    List<RestaurantMenu> findByRestaurantOrderByMenuId(Restaurant restaurant);
}