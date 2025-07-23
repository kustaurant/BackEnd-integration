
package com.kustaurant.kustaurant.restaurant.tier.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.tier.RestaurantChartService;
import com.kustaurant.kustaurant.restaurant.tier.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.tier.argument_resolver.LocationList;
import com.kustaurant.kustaurant.restaurant.tier.argument_resolver.SituationList;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class RestaurantTierWebController {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantChartService restaurantChartService;

    public static final Integer tierPageSize = 40;
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
            @RequestParam(value = "page", defaultValue = "1") int page,
            @CuisineList List<String> cuisines,
            @SituationList List<Integer> situations,
            @LocationList List<String> locations,
            @AuthUser AuthUserInfo user,
            HttpServletRequest request
    ) {
        // page를 0부터 시작하도록 처리
        if (page < 1) {
            page = 0;
        } else {
            page--;
        }

        // DB 조회
        List<RestaurantTierDTO> tierRestaurants = restaurantChartService.findByConditions(cuisines, situations, locations, null, true, user.id());

        Pageable pageable = PageRequest.of(page, tierPageSize);

        model.addAttribute("isJH", cuisines != null && cuisines.contains("JH"));

        model.addAttribute("cuisines", cuisines);
        model.addAttribute("situations", situations);
        model.addAttribute("locations", locations);
        model.addAttribute("currentPage","tier");
        model.addAttribute("evaluationsCount", evaluationRepository.countAllByStatus("ACTIVE"));
        model.addAttribute("paging", convertToPage(tierRestaurants, pageable));
        model.addAttribute("queryString", getQueryStringWithoutPage(request));

        return "tier";
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

    public Page<RestaurantTierDTO> convertToPage(List<RestaurantTierDTO> dataList, Pageable pageable) {
        int totalSize = dataList.size();
        int totalPages = (int) Math.ceil((double) totalSize / pageable.getPageSize());

        // 현재 페이지가 총 페이지 수보다 큰 경우 마지막 페이지로 이동
        if (pageable.getPageNumber() >= totalPages && totalPages > 0) {
            pageable = PageRequest.of(totalPages - 1, pageable.getPageSize());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), totalSize);

        return new PageImpl<>(dataList.subList(start, end), pageable, totalSize);
    }

    // 티어표 화면 이전
//    @GetMapping("/tier")
//    public String tierPre(
//            Model model,
//            @RequestParam(value = "page", defaultValue = "0") int page,
//            @RequestParam(value = "cuisine", required = false, defaultValue = "전체") String cuisine,
//            @RequestParam(value = "situation", required = false, defaultValue = "전체") String situation,
//            @RequestParam(value = "position", required = false, defaultValue = "전체") String position,
//            Principal principal
//    ) {
//        // 지도 정보 넣어주기
//        int positionIndex = getPositionIndex(position);
//        model.addAttribute("mapLatitude", latitudeArray[positionIndex]);
//        model.addAttribute("mapLongitude", longitudeArray[positionIndex]);
//        model.addAttribute("mapZoom", zoomArray[positionIndex]);
//        model.addAttribute("positionIndex", positionIndex);
//        if (principal != null) {
//            User user = customOAuth2UserService.getUser(principal.getName());
//            List<Restaurant> favoriteRestaurantIdList = new ArrayList<>();
//            for (RestaurantFavorite favorite : user.getRestaurantFavoriteList()) {
//                favoriteRestaurantIdList.add(favorite.getRestaurant());
//            }
//            model.addAttribute("favoriteRestaurantList", convertObjectToJson(favoriteRestaurantIdList));
//        }
//        //
//        Pageable pageable = PageRequest.of(page, tierPageSize);
//        if (situation.equals("전체") && cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체
//            List<RestaurantTierDataClass> restaurantList = evaluationService.getAllRestaurantTierDataClassList(position, principal, page, false);
//            model.addAttribute("situation", "전체");
//            model.addAttribute("paging", convertToPage(restaurantList, pageable));
//            // 지도 정보 넣어주기
//            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
//        } else if (cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체 아님
//            EnumSituation enumSituation = EnumSituation.valueOf(situation);
//            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
//            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListBySituation(situationObject, position, principal, page, false);
//            model.addAttribute("situation", enumSituation.getValue());
//            model.addAttribute("paging", convertToPage(restaurantList, pageable));
//            // 지도 정보 넣어주기
//            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
//        } else if (situation.equals("전체")) { // 종류: 전체 아님 & 상황: 전체
//            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisine(cuisine, position, principal, page, false);
//            model.addAttribute("paging", convertToPage(restaurantList, pageable));
//            model.addAttribute("situation", "전체");
//            // 지도 정보 넣어주기
//            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
//        } else { // 종류: 전체 아님 & 상황: 전체 아님
//            EnumSituation enumSituation = EnumSituation.valueOf(situation);
//            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
//            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisineAndSituation(cuisine, situationObject, position, principal, page, false);
//            model.addAttribute("situation", enumSituation.getValue());
//            model.addAttribute("paging", convertToPage(restaurantList, pageable));
//            // 지도 정보 넣어주기
//            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
//        }
//        model.addAttribute("situationQueryParameter", situation);
//        model.addAttribute("positionQueryParameter", position);
//        model.addAttribute("currentPage","tier");
//        model.addAttribute("cuisine", cuisine);
//        model.addAttribute("position", position);
//        model.addAttribute("evaluationsCount", evaluationRepository.countAllByStatus("ACTIVE"));
//        return "tier";
//    }
}
