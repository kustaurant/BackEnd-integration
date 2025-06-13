package com.kustaurant.kustaurant.restaurant.application.service.query.port;

import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RestaurantQueryRepository {
    // 티어표
    List<Restaurant> findAll(Specification<RestaurantEntity> spec);
    List<Restaurant> findAll(Specification<RestaurantEntity> spec, Pageable pageable);
    // 검색
    List<Restaurant> search(String[] kwList);
}
