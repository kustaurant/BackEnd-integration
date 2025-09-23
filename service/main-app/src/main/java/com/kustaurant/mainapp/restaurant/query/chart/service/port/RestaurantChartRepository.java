package com.kustaurant.mainapp.restaurant.query.chart.service.port;

import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition;
import org.springframework.data.domain.Page;

public interface RestaurantChartRepository {

    Page<Long> getRestaurantIdsWithPage(ChartCondition condition);
}
