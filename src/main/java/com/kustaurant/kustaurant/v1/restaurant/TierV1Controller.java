package com.kustaurant.kustaurant.v1.restaurant;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.LocationList;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.SituationList;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantTierDTO;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantTierMapDTO;
import java.util.List;
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

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TierV1Controller {

    private final RestaurantChartService restaurantChartService;

    @GetMapping(value = "/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartList(
            @CuisineList List<String> cuisines,
            @SituationList List<Integer> situations,
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
        ChartCondition condition = new ChartCondition(cuisines, situations.stream().map(i -> (long) i).toList(), locations, TierFilter.ALL);
        Pageable pageable = PageRequest.of(page, limit);
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, pageable, user.id());

        return new ResponseEntity<>(restaurants.stream().map(RestaurantTierDTO::fromV2).toList(), HttpStatus.OK);
    }

    @GetMapping(value = "/auth/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartListWithAuth(
            @CuisineList List<String> cuisines,
            @SituationList List<Integer> situations,
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
        ChartCondition condition = new ChartCondition(cuisines, situations.stream().map(i -> (long) i).toList(), locations, TierFilter.ALL);
        Pageable pageable = PageRequest.of(page, limit);
        Page<RestaurantCoreInfoDto> restaurants = restaurantChartService.findByConditions(condition, pageable, user.id());

        return new ResponseEntity<>(restaurants.stream().map(RestaurantTierDTO::fromV2).toList(), HttpStatus.OK);
    }

    @GetMapping("/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @CuisineList List<String> cuisines,
            @SituationList List<Integer> situations,
            @LocationList List<String> locations,
            @AuthUser AuthUserInfo user
    ) {
        ChartCondition condition = new ChartCondition(cuisines, situations.stream().map(i -> (long) i).toList(), locations, TierFilter.ALL);
        com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO response = restaurantChartService.getRestaurantTierMapDto(
                condition, user.id());
        return new ResponseEntity<>(RestaurantTierMapDTO.fromV2(response), HttpStatus.OK);
    }

    @GetMapping("/auth/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfoWithAuth(
            @CuisineList List<String> cuisines,
            @SituationList List<Integer> situations,
            @LocationList List<String> locations,
            @AuthUser AuthUserInfo user
    ) {
        ChartCondition condition = new ChartCondition(cuisines, situations.stream().map(i -> (long) i).toList(), locations, TierFilter.ALL);
        com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO response = restaurantChartService.getRestaurantTierMapDto(
                condition, user.id());
        return new ResponseEntity<>(RestaurantTierMapDTO.fromV2(response), HttpStatus.OK);
    }
}
