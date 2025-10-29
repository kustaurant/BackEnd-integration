package com.kustaurant.kustaurant.restaurant.query.chart.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.chart.controller.dto.RestaurantChartResponse;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ding
 * @since 2024.7.10.
 * description: tier controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestaurantChartApiController implements RestaurantChartApiDoc {

    private final RestaurantChartService restaurantChartService;

    @GetMapping(value = "/v2/tier")
    public ResponseEntity<RestaurantChartResponse> getTierChartList(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, user.id());

        RestaurantChartResponse result = new RestaurantChartResponse(restaurants.getContent(), restaurants.hasNext());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/v2/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        RestaurantTierMapDTO restaurants = restaurantChartService.getRestaurantTierMapDto(condition, user.id());

        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }
}
