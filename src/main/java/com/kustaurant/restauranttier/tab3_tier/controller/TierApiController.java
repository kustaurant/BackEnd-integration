package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.tab3_tier.dto.Coordinate;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierMapDTO;
import com.kustaurant.restauranttier.tab3_tier.etc.LocationEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.MapVariable;
import com.kustaurant.restauranttier.tab3_tier.etc.SituationEnum;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Ding
 * @since 2024.7.10.
 * description: tier controller
 */
@RestController
@RequestMapping("/api/v1/tier")
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters provided", content = {@Content(mediaType = "application/json")})
})
public class TierApiController {

    private final RestaurantApiRepository restaurantApiRepository;
    private final RestaurantApiService restaurantApiService;

    @Operation(summary = "티어표 리스트 불러오기", description = "파라미터로 받는 page(1부터 카운트)의 limit개의 식당 리스트를 반환합니다. 현재는 파라미터와 무관한 데이터를 반환합니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantTierDTO.class)))})
    })
    @GetMapping
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartList(
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            String cuisines,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            String situations,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            String locations,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer limit
    ) {
        List<RestaurantTierDTO> responseList = new ArrayList<>();
        Page<Restaurant> restaurants = restaurantApiService.getRestaurantsByCuisinesAndLocationsWithPage(cuisines, locations, 1, true, page, limit);

        for (int i = 0; i < limit; i++) {
            try {
                int ranking = (page - 1) * limit + i + 1;
                Restaurant restaurant = restaurants.toList().get(i);
                responseList.add(RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, ranking, randomBoolean(), randomBoolean()));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    public static boolean randomBoolean() {
        Random random = new Random();
        return random.nextDouble() < 0.4; // 30% 확률로 true, 70% 확률로 false
    }

    @Operation(summary = "지도 식당 정보 불러오기", description = "[설명]" +
            "\n\n현재 선택한 카테고리에 맞는 전체 식당 정보를 불러옵니다. (현재는 카테고리와 상관없이 고정된 정보를 반환합니다.) " +
            "\n\n티어가 있는 식당(tieredRestaurants)은 zoom에 상관없이 항상 보입니다. " +
            "\n\n티어가 없는 식당은 zoom에 따라 보이는게 달라집니다. " +
            "\n\n티어가 없는 식당(nonTieredRestaurants)은 zoom과 식당 리스트가 함께 주어집니다. " +
            "\n\n현재 지도의 zoom이 17이라면 zoom이 17보다 같거나 작은(.., 16, 17) 식당 리스트를 지도에 표시하시면 됩니다." +
            "\n\n즐겨찾기 리스트에 있는 식당은 지도에서 즐겨찾기 마커로 보여야합니다. (티어 마커나 다른 마커가 아닌 즐겨찾기 마커로 보여야함)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantTierMapDTO.class))})
    })
    @GetMapping("/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            String cuisines,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            String situations,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            String locations
    ) {
        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<Restaurant> tieredRestaurants = restaurantApiService.getRestaurantsByCuisinesAndLocations(cuisines, locations, 1, false);
        List<Restaurant> nonTieredRestaurants = restaurantApiService.getRestaurantsByCuisinesAndLocations(cuisines, locations, -1, false);

        // 2. 상황으로 필터링하기
        List<RestaurantTierDTO> tieredRestaurantTierDTOs;
        List<RestaurantTierDTO> nonTieredRestaurantTierDTOs;
        if (!situations.contains("ALL")) {
            // 파라미터 파싱
            try {
                List<Integer> situationList = Arrays.stream(situations.split(","))
                        .map(Integer::parseInt)
                        .toList();
                // 상황 정보로 필터링
                // TODO: 즐겨찾기, 평가 여부 추가해야됨
                tieredRestaurantTierDTOs = tieredRestaurants.stream()
                        .filter(res -> restaurantApiService.isSituationContainRestaurant(situationList, res))
                        .map(res -> RestaurantTierDTO.convertRestaurantToTierDTO(res, null, randomBoolean(), randomBoolean()))
                        .toList();
                nonTieredRestaurantTierDTOs = nonTieredRestaurants.stream()
                        .filter(res -> restaurantApiService.isSituationContainRestaurant(situationList, res))
                        .map(res -> RestaurantTierDTO.convertRestaurantToTierDTO(res, null, randomBoolean(), randomBoolean()))
                        .toList();
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            tieredRestaurantTierDTOs = tieredRestaurants.stream()
                    .map(res -> RestaurantTierDTO.convertRestaurantToTierDTO(res, null, randomBoolean(), randomBoolean()))
                    .toList();
            nonTieredRestaurantTierDTOs = nonTieredRestaurants.stream()
                    .map(res -> RestaurantTierDTO.convertRestaurantToTierDTO(res, null, randomBoolean(), randomBoolean()))
                    .toList();
        }

        // 3. 응답 생성하기
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();
        // 3.1 즐겨찾기 리스트
        // TODO: 즐겨찾기 추가해야됨.
        // 3.2 티어가 있는 식당 리스트
        response.setTieredRestaurants(tieredRestaurantTierDTOs);
        // 3.3 티어가 없는 식당 리스트
        List<RestaurantTierDTO> nonTier16 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 0)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier17 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 1)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier18 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 2)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        List<RestaurantTierDTO> nonTier19 = IntStream.range(0, nonTieredRestaurantTierDTOs.size())
                .filter(i ->  i % 4 == 3)
                .mapToObj(nonTieredRestaurantTierDTOs::get)
                .toList();
        response.insertZoomAndRestaurants(16, nonTier16);
        response.insertZoomAndRestaurants(17, nonTier17);
        response.insertZoomAndRestaurants(18, nonTier18);
        response.insertZoomAndRestaurants(19, nonTier19);
        // 3.4 폴리곤 좌표 리스트의 리스트
        if (!locations.contains(("ALL"))) {
            try {
                List<Integer> locationList = Arrays.stream(locations.split(","))
                        .map(el -> Integer.parseInt(el.substring(1)))
                        .toList();
                for (int i = 0; i < MapVariable.LIST_OF_COORD_LIST.size(); i++) {
                    if (locationList.contains(i + 1)) {
                        response.getSolidPolygonCoordsList().add(MapVariable.LIST_OF_COORD_LIST.get(i));
                    } else {
                        response.getDashedPolygonCoordsList().add(MapVariable.LIST_OF_COORD_LIST.get(i));
                    }
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            response.setSolidPolygonCoordsList(MapVariable.LIST_OF_COORD_LIST);
        }
        // 3.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(MapVariable.findMinMaxCoordinates(response.getSolidPolygonCoordsList()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
