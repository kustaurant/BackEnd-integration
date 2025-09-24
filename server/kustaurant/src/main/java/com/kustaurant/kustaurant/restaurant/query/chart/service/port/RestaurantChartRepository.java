package com.kustaurant.kustaurant.restaurant.query.chart.service.port;

import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import org.springframework.data.domain.Page;

public interface RestaurantChartRepository {

    Page<Long> getRestaurantIdsWithPage(ChartCondition condition);
}
