package com.kustaurant.kustaurant.restaurant.query.chart.controller;

import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.chart.service.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO;
import io.micrometer.observation.annotation.Observed;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RestaurantChartController {

    private final EvaluationQueryRepository evaluationQueryRepository;
    private final RestaurantChartService restaurantChartService;

    public static final Integer TIER_PAGE_SIZE = 30;

    // 티어표 화면
    @GetMapping("/tier")
    @Observed(name = "tier.controller")
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
        model.addAttribute("aiTier", condition.aiTier());

        return "restaurant/tier";
    }

    @GetMapping("/web/api/tier/map")
    @Observed(name = "tier.controller")
    @ResponseBody
    public ResponseEntity<RestaurantTierMapDTO> tierMapInfo(
            @ChartCond ChartCondition condition,
            @AuthUser AuthUserInfo user
    ) {
        RestaurantTierMapDTO mapInfo = restaurantChartService.getRestaurantTierMapDto(condition, user.id());
        return new ResponseEntity<>(mapInfo, HttpStatus.OK);
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
