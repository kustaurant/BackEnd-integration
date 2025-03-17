package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantHashtagRepository extends JpaRepository<RestaurantHashtag,Integer> {
}
