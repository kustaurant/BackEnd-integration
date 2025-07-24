package com.kustaurant.kustaurant.restaurant.tier.service.port;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface RestaurantChartRepository {

    Page<RestaurantTierDTO> findByCondition(ChartCondition condition, Pageable pageable, Long userId);
}
