package com.kustaurant.mainapp.restaurant.query.draw;

import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import java.util.List;

public interface RestaurantDrawRepository {

    Restaurant getById(Long id);

    List<Long> getRestaurantIds(ChartCondition condition);
}
