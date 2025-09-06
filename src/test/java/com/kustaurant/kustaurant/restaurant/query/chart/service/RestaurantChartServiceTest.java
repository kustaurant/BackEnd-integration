package com.kustaurant.kustaurant.restaurant.query.chart.service;

import static org.junit.jupiter.api.Assertions.*;

import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class RestaurantChartServiceTest {

    @Autowired
    private RestaurantChartService restaurantChartService;

    @Test
    void getMapData() {
        ChartCondition condition = new ChartCondition(
                null, null, null, TierFilter.ALL
        );
        List<RestaurantCoreInfoDto> tieredRestaurantCoreInfos = restaurantChartService.findByConditions(
                condition.changeTierFilter(TierFilter.WITH_TIER), null, null).toList();
        System.out.println("티어 개수: " + tieredRestaurantCoreInfos.size());
        List<RestaurantCoreInfoDto> nonTieredRestaurantCoreInfos = restaurantChartService.findByConditions(
                condition.changeTierFilter(TierFilter.WITHOUT_TIER), null, null).toList();
        System.out.println("논티어 개수: " + nonTieredRestaurantCoreInfos.size());
    }
}