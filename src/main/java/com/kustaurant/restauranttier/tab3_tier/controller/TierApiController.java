package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierMapDTO;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
            @Parameter(example = "ONE,SEVEN 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, ONE:혼밥, TWO:2~4인, THREE:5인 이상, FOUR:단체 회식, FIVE:배달, SIX:야식, SEVEN:친구 초대, EIGHT:데이트, NINE:소개팅)")
            String situations,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            String locations,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "30") Integer limit
    ) {
        List<RestaurantTierDTO> responseList = new ArrayList<>();
        List<Restaurant> all = restaurantApiRepository.findByStatus("ACTIVE");

        for (int i = 0; i < limit; i++) {
            int index = (page - 1) * limit + i;
            try {
                Restaurant restaurant = all.get(index);
                responseList.add(RestaurantTierDTO.convertRestaurantToTierDTO(restaurant, index + 1, randomBoolean(), randomBoolean()));
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
            @Parameter(example = "ONE,SEVEN 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, ONE:혼밥, TWO:2~4인, THREE:5인 이상, FOUR:단체 회식, FIVE:배달, SIX:야식, SEVEN:친구 초대, EIGHT:데이트, NINE:소개팅)")
            String situations,
            @RequestParam(defaultValue = "ALL")
            @Parameter(example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            String locations
    ) {
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();

        List<Restaurant> tierTempRestaurants = restaurantApiRepository.findByStatusAndRestaurantPosition("ACTIVE", "중문~어대");

        List<Restaurant> favoriteRestaurants = new ArrayList<>();
        favoriteRestaurants.add(tierTempRestaurants.get(0));
        favoriteRestaurants.add(tierTempRestaurants.get(2));
        favoriteRestaurants.add(tierTempRestaurants.get(4));
        favoriteRestaurants.add(tierTempRestaurants.get(6));
        favoriteRestaurants.add(tierTempRestaurants.get(8));
        favoriteRestaurants.add(tierTempRestaurants.get(10));
        response.setFavoriteRestaurants(favoriteRestaurants.stream()
                .filter(source -> source.getMainTier() != -1)
                .map(source -> RestaurantTierDTO.convertRestaurantToTierDTO(source, null, randomBoolean(), randomBoolean()))
                .toList());

        response.setTieredRestaurants(tierTempRestaurants.stream()
                .filter(source -> source.getMainTier() != -1)
                .map(source -> RestaurantTierDTO.convertRestaurantToTierDTO(source, null, randomBoolean(), randomBoolean()))
                .toList());
        List<Restaurant> nonTierTempRestaurants = restaurantApiRepository.findByStatus("ACTIVE");
        List<RestaurantTierDTO> nonTierTempRestaurants2 = nonTierTempRestaurants.stream()
                        .filter(source -> source.getMainTier() == -1)
                        .map(source -> RestaurantTierDTO.convertRestaurantToTierDTO(source, null, randomBoolean(), randomBoolean()))
                                .toList();
        List<RestaurantTierDTO> nonTier16 = IntStream.range(0, nonTierTempRestaurants2.size())
                        .filter(i ->  i % 4 == 0)
                                .mapToObj(nonTierTempRestaurants2::get)
                                        .toList();
        List<RestaurantTierDTO> nonTier17 = IntStream.range(0, nonTierTempRestaurants2.size())
                .filter(i ->  i % 4 == 1)
                .mapToObj(nonTierTempRestaurants2::get)
                .toList();
        List<RestaurantTierDTO> nonTier18 = IntStream.range(0, nonTierTempRestaurants2.size())
                .filter(i ->  i % 4 == 2)
                .mapToObj(nonTierTempRestaurants2::get)
                .toList();
        List<RestaurantTierDTO> nonTier19 = IntStream.range(0, nonTierTempRestaurants2.size())
                .filter(i ->  i % 4 == 3)
                .mapToObj(nonTierTempRestaurants2::get)
                .toList();

        response.insertZoomAndRestaurants(16, nonTier16);
        response.insertZoomAndRestaurants(17, nonTier17);
        response.insertZoomAndRestaurants(18, nonTier18);
        response.insertZoomAndRestaurants(19, nonTier19);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
