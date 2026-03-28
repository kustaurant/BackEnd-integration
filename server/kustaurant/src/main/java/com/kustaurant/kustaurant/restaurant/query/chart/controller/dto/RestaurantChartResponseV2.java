package com.kustaurant.kustaurant.restaurant.query.chart.controller.dto;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDtoV2;

import java.util.List;

public record RestaurantChartResponseV2(
        List<RestaurantCoreInfoDtoV2> restaurants,
        boolean hasNext
) {

}
