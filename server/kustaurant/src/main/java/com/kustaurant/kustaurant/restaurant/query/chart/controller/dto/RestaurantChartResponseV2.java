package com.kustaurant.kustaurant.restaurant.query.chart.controller.dto;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDtoV2;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "2026.04.03")
public record RestaurantChartResponseV2(
        List<RestaurantCoreInfoDtoV2> restaurants,
        boolean hasNext
) {

}
