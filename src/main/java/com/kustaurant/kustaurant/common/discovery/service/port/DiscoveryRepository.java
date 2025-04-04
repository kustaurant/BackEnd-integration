package com.kustaurant.kustaurant.common.discovery.service.port;


import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface DiscoveryRepository {
    // 검색결과 페이징
    List<Restaurant> findAll(Specification<RestaurantEntity> spec);
    List<Restaurant> findAll(Specification<RestaurantEntity> spec, Pageable pageable);
}
