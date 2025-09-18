package com.kustaurant.kustaurant.restaurant.query.draw;

import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import java.util.List;

public interface RestaurantDrawRepository {

    Restaurant getById(Long id);

    List<Long> getRestaurantIds(ChartCondition condition);
}
