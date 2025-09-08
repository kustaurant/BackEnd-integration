package com.kustaurant.kustaurant.restaurant.query.chart.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.LocationList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.SituationList;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<RestaurantCoreInfoDto>> getTierChartList(
            @CuisineList List<String> cuisines,
            @SituationList List<Long> situations,
            @LocationList List<String> locations,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer limit,
            @AuthUser AuthUserInfo user
    ) {
        // page 0부터 시작하게 수정
        page--;
        if (page < 0) {
            page = 0;
        }
        // 조회
        ChartCondition condition = new ChartCondition(cuisines, situations, locations, TierFilter.ALL);
        Pageable pageable = PageRequest.of(page, limit);
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, pageable, user.id());

        return new ResponseEntity<>(restaurants.getContent(), HttpStatus.OK);
    }

    @GetMapping("/v2/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @CuisineList List<String> cuisines,
            @SituationList List<Long> situations,
            @LocationList List<String> locations,
            @AuthUser AuthUserInfo user
    ) {
        ChartCondition condition = new ChartCondition(cuisines, situations, locations, TierFilter.ALL);
        return new ResponseEntity<>(
                restaurantChartService.getRestaurantTierMapDto(condition, user.id()),
                HttpStatus.OK
        );
    }
}
