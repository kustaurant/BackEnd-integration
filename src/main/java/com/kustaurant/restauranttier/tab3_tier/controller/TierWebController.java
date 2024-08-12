
package com.kustaurant.restauranttier.tab3_tier.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab3_tier.entity.Situation;
import com.kustaurant.restauranttier.tab3_tier.etc.EnumSituation;
import com.kustaurant.restauranttier.tab3_tier.etc.RestaurantTierDataClass;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.repository.EvaluationRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.SituationRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab3_tier.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TierWebController {
    //
    private final SituationRepository situationRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationService evaluationService;
    private final CustomOAuth2UserService customOAuth2UserService;

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

    // 티어표 화면
    @GetMapping("/tier")
    public String tier(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "cuisine", required = false, defaultValue = "전체") String cuisine,
            @RequestParam(value = "situation", required = false, defaultValue = "전체") String situation,
            @RequestParam(value = "position", required = false, defaultValue = "전체") String position,
            Principal principal
    ) {
        // 지도 정보 넣어주기
        int positionIndex = getPositionIndex(position);
        model.addAttribute("mapLatitude", latitudeArray[positionIndex]);
        model.addAttribute("mapLongitude", longitudeArray[positionIndex]);
        model.addAttribute("mapZoom", zoomArray[positionIndex]);
        model.addAttribute("positionIndex", positionIndex);
        if (principal != null) {
            User user = customOAuth2UserService.getUser(principal.getName());
            List<Restaurant> favoriteRestaurantIdList = new ArrayList<>();
            for (RestaurantFavorite favorite : user.getRestaurantFavoriteList()) {
                favoriteRestaurantIdList.add(favorite.getRestaurant());
            }
            model.addAttribute("favoriteRestaurantList", convertObjectToJson(favoriteRestaurantIdList));
        }
        //
        Pageable pageable = PageRequest.of(page, tierPageSize);
        if (situation.equals("전체") && cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getAllRestaurantTierDataClassList(position, principal, page, false);
            model.addAttribute("situation", "전체");
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
            // 지도 정보 넣어주기
            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
        } else if (cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListBySituation(situationObject, position, principal, page, false);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
            // 지도 정보 넣어주기
            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
        } else if (situation.equals("전체")) { // 종류: 전체 아님 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisine(cuisine, position, principal, page, false);
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
            model.addAttribute("situation", "전체");
            // 지도 정보 넣어주기
            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
        } else { // 종류: 전체 아님 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisineAndSituation(cuisine, situationObject, position, principal, page, false);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
            // 지도 정보 넣어주기
            model.addAttribute("restaurantList", convertObjectToJson(restaurantList));
        }
        model.addAttribute("situationQueryParameter", situation);
        model.addAttribute("positionQueryParameter", position);
        model.addAttribute("currentPage","tier");
        model.addAttribute("cuisine", cuisine);
        model.addAttribute("position", position);
        model.addAttribute("evaluationsCount", evaluationRepository.countAllByStatus("ACTIVE"));
        return "tier";
    }

    public Page<RestaurantTierDataClass> convertToPage(List<RestaurantTierDataClass> dataList, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dataList.size());

        return new PageImpl<>(dataList.subList(start, end), pageable, dataList.size());
    }

    // List 전체가 있는 html 코드 반환
    @GetMapping("/api/list/tier")
    public String getTierList(
            Model model,
            @RequestParam(value = "cuisine", required = false, defaultValue = "전체") String cuisine,
            @RequestParam(value = "situation", required = false, defaultValue = "전체") String situation,
            @RequestParam(value = "position", required = false, defaultValue = "전체") String position,
            Principal principal
    ) {
        if (situation.equals("전체") && cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getAllRestaurantTierDataClassList(position, principal, 0, true);
            model.addAttribute("situation", "전체");
            model.addAttribute("paging", restaurantList);
        } else if (cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListBySituation(situationObject, position, principal, 0, true);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", restaurantList);
        } else if (situation.equals("전체")) { // 종류: 전체 아님 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisine(cuisine, position, principal, 0, true);
            model.addAttribute("paging", restaurantList);
            model.addAttribute("situation", "전체");
        } else { // 종류: 전체 아님 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisineAndSituation(cuisine, situationObject, position, principal, 0, true);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", restaurantList);
        }
        model.addAttribute("cuisine", cuisine);
        model.addAttribute("position", position);
        return "tierTable";
    }

    @GetMapping("/api/page/tier")
    public String getPageList(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "cuisine", required = false, defaultValue = "전체") String cuisine,
            @RequestParam(value = "situation", required = false, defaultValue = "전체") String situation,
            @RequestParam(value = "position", required = false, defaultValue = "전체") String position,
            Principal principal
    ) {
        Pageable pageable = PageRequest.of(page, tierPageSize);
        if (situation.equals("전체") && cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getAllRestaurantTierDataClassList(position, principal, page, false);
            model.addAttribute("situation", "전체");
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
        } else if (cuisine.equals("전체")) { // 종류: 전체 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListBySituation(situationObject, position, principal, page, false);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
        } else if (situation.equals("전체")) { // 종류: 전체 아님 & 상황: 전체
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisine(cuisine, position, principal, page, false);
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
            model.addAttribute("situation", "전체");
        } else { // 종류: 전체 아님 & 상황: 전체 아님
            EnumSituation enumSituation = EnumSituation.valueOf(situation);
            Situation situationObject = situationRepository.findBySituationName(enumSituation.getValue());
            List<RestaurantTierDataClass> restaurantList = evaluationService.getRestaurantTierDataClassListByCuisineAndSituation(cuisine, situationObject, position, principal, page, false);
            model.addAttribute("situation", enumSituation.getValue());
            model.addAttribute("paging", convertToPage(restaurantList, pageable));
        }
        model.addAttribute("position", position);
        model.addAttribute("cuisine", cuisine);
        return "tierTable";
    }
}
