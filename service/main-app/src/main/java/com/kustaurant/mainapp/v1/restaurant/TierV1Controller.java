package com.kustaurant.mainapp.v1.restaurant;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.mainapp.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.mainapp.v1.restaurant.response.RestaurantTierDTO;
import com.kustaurant.mainapp.v1.restaurant.response.RestaurantTierMapDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TierV1Controller implements TierV1Doc {

    private final RestaurantChartService restaurantChartService;

    @GetMapping(value = "/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartList(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, user.id());

        return new ResponseEntity<>(restaurants.stream().map(RestaurantTierDTO::fromV2).toList(), HttpStatus.OK);
    }

    @GetMapping(value = "/auth/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartListWithAuth(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, user.id());

        return new ResponseEntity<>(restaurants.stream().map(RestaurantTierDTO::fromV2).toList(), HttpStatus.OK);
    }

    @GetMapping("/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantTierMapDTO response = restaurantChartService.getRestaurantTierMapDto(
                condition, user.id());
        return new ResponseEntity<>(RestaurantTierMapDTO.fromV2(response), HttpStatus.OK);
    }

    @GetMapping("/auth/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfoWithAuth(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantTierMapDTO response = restaurantChartService.getRestaurantTierMapDto(
                condition, user.id());
        return new ResponseEntity<>(RestaurantTierMapDTO.fromV2(response), HttpStatus.OK);
    }
}
