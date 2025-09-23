package com.kustaurant.mainapp.restaurant.query.draw;

import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.mainapp.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class DrawController {

    private final RestaurantDrawService restaurantDrawService;

    // 메뉴추천 화면
    @GetMapping("/recommend")
    public String recommend(Model model) {

        model.addAttribute("currentPage", "recommend");
        List<String> cuisines = new ArrayList<>(Arrays.asList("한식","일식","중식","양식","아시안","고기","치킨","햄버거","분식","해산물","술집","샐러드","카페","베이커리","기타","전체"));

        model.addAttribute("cuisines", cuisines);
        return "restaurant/recommend";
    }

    // 메뉴 리스트 받아오기
    @GetMapping("/web/api/recommend")
    public ResponseEntity<List<RestaurantCoreInfoDto>> getRestaurantListForCuisine(
            @RequestParam(value = "cuisine", defaultValue = "전체") String cuisine,
            @RequestParam(value = "location",defaultValue = "전체") String location
    ) {
        String[] cuisinesArray = cuisine.split("-");
        List<String> cuisinesList = Arrays.asList(cuisinesArray);
        for (String item : cuisinesList) {
            if(item.equals("햄버거")){
                item="햄버거/피자";
            }
            if (item.equals("카페")) {
                item="카페/디저트";
            }
        }
        if (cuisinesList.contains("전체")) {
            cuisinesList = null;
        }
        List<String> locations = location == null || location.equals("전체") ? null : List.of(location);

        ChartCondition condition = new ChartCondition(cuisinesList, null, locations,
                TierFilter.ALL, Pageable.ofSize(40));
        List<RestaurantCoreInfoDto> restaurants = restaurantDrawService.draw(condition);
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    // 랜덤으로 섞은 후 정확히 target 변수 만큼 의 식당을 반환하는 메소드
    private List<RestaurantEntity> getRandomSubList(List<RestaurantEntity> originalList, int targetSize) {
        List<RestaurantEntity> resultList = new ArrayList<>(originalList.size());
        Random rand = new Random();

        // 원본 리스트가 targetSize보다 작으면, 원본 리스트의 항목을 반복하여 추가
        while (resultList.size() < targetSize) {
            if (!originalList.isEmpty()) {
                resultList.addAll(originalList);
            } else {
                // 주어진 originalList가 비어있으면 빈 resultList 반환
                return resultList;
            }
        }

        // resultList를 랜덤으로 섞음
        Collections.shuffle(resultList, rand);

        // 결과 리스트에서 targetSize만큼 잘라서 반환
        return new ArrayList<>(resultList.subList(0, targetSize));
    }


    // id에 해당 하는 식당 정보 반환
    @GetMapping("/web/api/recommend/restaurant")
    public ResponseEntity<Restaurant> recommendRestaurant(
            @RequestParam(name = "restaurantId") Long restaurantId
    ) {
        Restaurant restaurant = restaurantDrawService.getById(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

}
