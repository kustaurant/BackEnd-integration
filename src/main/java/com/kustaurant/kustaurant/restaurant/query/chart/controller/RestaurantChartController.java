
package com.kustaurant.kustaurant.restaurant.query.chart.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RestaurantChartController {
    private final EvaluationQueryRepository evaluationQueryRepository;
    private final RestaurantChartService restaurantChartService;

    public static final Integer TIER_PAGE_SIZE = 30;
    // 티어표 지도 중앙 좌표
    // 인덱스 0번.전체 | 1번.건입~중문 | 2번.중문~어대 | 3번.후문 | 4번.정문 | 5번.구의역
    private float[] latitudeArray = {37.542318f, 37.541518f, 37.545520f, 37.545750f, 37.538512f, 37.537962f};
    private float[] longitudeArray = {127.076467f, 127.069190f, 127.069550f, 127.076875f, 127.077239f, 127.085855f};
    private int[] zoomArray = {15, 15, 15, 15, 15, 16};
    private int getPositionIndex(String position) {
        if (position.equals("전체"))
            return 0;
        if (position.equals("건입~중문"))
            return 1;
        if (position.equals("중문~어대"))
            return 2;
        if (position.equals("후문"))
            return 3;
        if (position.equals("정문"))
            return 4;
        if (position.equals("구의역"))
            return 5;
        return 0;
    }
    public String convertObjectToJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 티어표 화면 리팩토링
    @GetMapping("/tier")
    public String tier(
            Model model,
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user,
            HttpServletRequest request
    ) {
        Page<RestaurantCoreInfoDto> data = restaurantChartService.findByConditions(condition, user.id());

        List<String> cuisines = condition.cuisines();
        List<Long> situations = condition.situations();
        List<String> locations = condition.positions();

        model.addAttribute("isJH", cuisines != null && cuisines.contains("JH"));

        model.addAttribute("cuisines", cuisines);
        model.addAttribute("situations", situations);
        model.addAttribute("locations", locations);
        model.addAttribute("currentPage","tier");
        model.addAttribute("evaluationsCount", evaluationQueryRepository.countByStatus("ACTIVE"));
        model.addAttribute("paging", data);
        model.addAttribute("queryString", getQueryStringWithoutPage(request));

        // 지도 정보 넣어주기
        model.addAttribute("restaurantList", data);
        List<RestaurantCoreInfoDto> favorites = new ArrayList<>();
        for (RestaurantCoreInfoDto d : data) {
            if (d.getIsFavorite()) {
                favorites.add(d);
            }
        }
        model.addAttribute("favoriteRestaurantList", favorites);
        model.addAttribute("mapLatitude", latitudeArray[0]);
        model.addAttribute("mapLongitude", longitudeArray[0]);
        model.addAttribute("mapZoom", zoomArray[0]);
        model.addAttribute("locationIndex", 0);

        return "restaurant/tier";
    }

    private String getQueryStringWithoutPage(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .filter(entry -> !entry.getKey().equals("page"))
                .flatMap(entry -> {
                    String key = entry.getKey();
                    return Arrays.stream(entry.getValue()).map(value -> key + "=" + value);
                })
                .collect(Collectors.joining("&"));
    }
}
