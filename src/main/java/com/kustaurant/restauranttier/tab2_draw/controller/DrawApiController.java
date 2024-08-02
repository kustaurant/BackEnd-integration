package com.kustaurant.restauranttier.tab2_draw.controller;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequiredArgsConstructor
public class DrawApiController {
    private final RestaurantApiService restaurantApiService;

    // 랜덤 뽑기할때 식당리스트 받아오기s
    @Operation(summary = "뽑기 버튼 클릭", description = "위치와 음식 종류에 맞는 식당 중 랜덤으로 30개를 반환합니다. 이 30개 중에서 하나를 뽑는 방식으로 뽑기가 진행됩니다. 뽑는 과정은 클라이언트에서 진행됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "draw response success", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Restaurant.class)))}),
            @ApiResponse(responseCode = "404", description = "draw response fail", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/api/v1/draw")
    public ResponseEntity<List<Restaurant>> getRestaurantListForCuisine(
            @RequestParam(value = "cuisine", defaultValue = "전체") String cuisine, @RequestParam(value = "location",defaultValue = "전체") String location, @RequestParam(value = "evaluation",defaultValue = "전체") String evaluation
    ) {
        String[] cuisinesArray = cuisine.split("-");
        List<String> cuisinesList = Arrays.asList(cuisinesArray);
        List<Restaurant> combinedRestaurantList = new ArrayList<>();
        // 음식 종류마다 location 과 일치하는 식당 리스트 반환해서 combinedRestaurantList에 추가
        for (String item : cuisinesList) {

            List<Restaurant> retaurantList = restaurantApiService.getRestaurantListByRandomPick(item,location);
            combinedRestaurantList.addAll(retaurantList);
        }
        // 랜덤으로 30개 선택
        return new ResponseEntity<>(getRandomSubList(combinedRestaurantList, 30), HttpStatus.OK);

    }


    // 랜덤으로 섞은 후 정확히 target 변수 만큼 의 식당을 반환하는 메소드
    private List<Restaurant> getRandomSubList(List<Restaurant> originalList, int targetSize) {
        List<Restaurant> resultList = new ArrayList<>(originalList.size());
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
}
