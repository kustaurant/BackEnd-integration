package com.kustaurant.kustaurant.restaurant.query.chart.controller.dto;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import java.util.List;

public record RestaurantChartResponse(
        List<RestaurantCoreInfoDto> restaurants,
        boolean hasNext
) {

}
