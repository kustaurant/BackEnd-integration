package com.kustaurant.kustaurant.restaurant.query.chart.service.port;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantChartRepository {

    Page<RestaurantCoreInfoDto> getChartRestaurantsByCondition(ChartCondition condition, Pageable pageable, Long userId);
}
