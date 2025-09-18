package com.kustaurant.kustaurant.restaurant.query.chart.service.port;

import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantChartRepository {

    Page<Long> getRestaurantIdsWithPage(ChartCondition condition);
}
