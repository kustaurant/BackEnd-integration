package com.kustaurant.kustaurant.api.restaurant;

import com.kustaurant.kustaurant.common.restaurant.domain.enums.LocationEnum;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantApiService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantTierService;
import com.kustaurant.kustaurant.global.UserService;
import com.kustaurant.kustaurant.global.apiUser.customAnno.JwtToken;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.common.restaurant.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.common.restaurant.argument_resolver.LocationList;
import com.kustaurant.kustaurant.common.restaurant.argument_resolver.SituationList;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantTierMapDTO;
import com.kustaurant.kustaurant.common.restaurant.constants.MapConstants;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Ding
 * @since 2024.7.10.
 * description: tier controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TierApiController {

    private final RestaurantTierService restaurantTierService;

    private final RestaurantApiService restaurantApiService;
    private final UserService userService;
    private final RestaurantFavoriteService restaurantFavoriteService;

    @Operation(summary = "티어표 리스트 불러오기", description = "파라미터로 받는 page(1부터 카운트)의 limit개의 식당 리스트를 반환합니다. 현재는 파라미터와 무관한 데이터를 반환합니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantRanking: **null일 수 있습니다.**\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - x: not null\n\n" +
            "   - y: not null\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantTierDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 잘못됨.")
    })
    @GetMapping(value = "/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartList(
            @Parameter(schema = @Schema(type = "string"), example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            @CuisineList List<String> cuisines,
            @Parameter(schema = @Schema(type = "string"), example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            @SituationList List<Integer> situations,
            @Parameter(schema = @Schema(type = "string"), example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            @LocationList List<String> locations,
            @RequestParam(defaultValue = "1") @Parameter(description = "페이지는 1부터 시작입니다.") Integer page,
            @RequestParam(defaultValue = "30") @Parameter(description = "한 페이지의 항목 개수입니다.") Integer limit,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        User user = userService.findUserById(userId);
        // page 0부터 시작하게 수정
        page--;
        // DB 조회
        List<RestaurantEntity> restaurants = restaurantTierService.findByConditionsWithPage(cuisines, situations, locations, null, true, page, limit).toList();

        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        // 순위 표시하기
        List<RestaurantTierDTO> responseList = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            try {
                // 순위
                Integer ranking = null;
                RestaurantEntity restaurant = restaurants.get(i);
                if (restaurant.getMainTier() > 0) {
                    ranking = page * limit + i + 1;
                }
                responseList.add(RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurant, ranking, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }

    @Operation(summary = "티어표 리스트 불러오기 (Auth 버전)", description = "파라미터로 받는 page(1부터 카운트)의 limit개의 식당 리스트를 반환합니다. 현재는 파라미터와 무관한 데이터를 반환합니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantRanking: **null일 수 있습니다.**\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - x: not null\n\n" +
            "   - y: not null\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantTierDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 잘못됨.")
    })
    @GetMapping(value = "/auth/tier")
    public ResponseEntity<List<RestaurantTierDTO>> getTierChartListWithAuth(
            @Parameter(schema = @Schema(type = "string"), example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            @CuisineList List<String> cuisines,
            @Parameter(schema = @Schema(type = "string"), example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            @SituationList List<Integer> situations,
            @Parameter(schema = @Schema(type = "string"), example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            @LocationList List<String> locations,
            @RequestParam(defaultValue = "1") @Parameter(description = "페이지는 1부터 시작입니다.") Integer page,
            @RequestParam(defaultValue = "30") @Parameter(description = "한 페이지의 항목 개수입니다.") Integer limit,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        User user = userService.findUserById(userId);
        // page 0부터 시작하게 수정
        page--;
        // DB 조회
        List<RestaurantEntity> restaurants = restaurantTierService.findByConditionsWithPage(cuisines, situations, locations, null, true, page, limit).toList();

        if (restaurants.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }
        // 순위 표시하기
        List<RestaurantTierDTO> responseList = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            try {
                // 순위
                Integer ranking = null;
                RestaurantEntity restaurant = restaurants.get(i);
                if (restaurant.getMainTier() > 0) {
                    ranking = page * limit + i + 1;
                }
                responseList.add(RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurant, ranking, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }



    @Operation(summary = "지도 식당 정보 불러오기", description = "[설명]" +
            "\n\n현재 선택한 카테고리에 맞는 전체 식당 정보를 불러옵니다. (현재는 카테고리와 상관없이 고정된 정보를 반환합니다.) " +
            "\n\n티어가 있는 식당(tieredRestaurants)은 zoom에 상관없이 항상 보입니다. " +
            "\n\n티어가 없는 식당은 zoom에 따라 보이는게 달라집니다. " +
            "\n\n티어가 없는 식당(nonTieredRestaurants)은 zoom과 식당 리스트가 함께 주어집니다. " +
            "\n\n현재 지도의 zoom이 17이라면 zoom이 17보다 같거나 작은(.., 16, 17) 식당 리스트를 지도에 표시하시면 됩니다." +
            "\n\n즐겨찾기 리스트에 있는 식당은 지도에서 즐겨찾기 마커로 보여야합니다. (티어 마커나 다른 마커가 아닌 즐겨찾기 마커로 보여야함)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - minZoom: not null\n\n" +
            "   - favoriteRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "   - tieredRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "   - nonTieredRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "       - restaurantId: not null\n\n" +
            "       - restaurantRanking: **null일 수 있습니다.**\n\n" +
            "       - restaurantName: not null\n\n" +
            "       - restaurantCuisine: not null\n\n" +
            "       - restaurantPosition: not null\n\n" +
            "       - restaurantImgUrl: not null\n\n" +
            "       - mainTier: not null\n\n" +
            "       - isEvaluated: not null\n\n" +
            "       - isFavorite: not null\n\n" +
            "       - x: not null\n\n" +
            "       - y: not null\n\n" +
            "       - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "       - restaurantScore: **null일 수 있습니다.**\n\n" +
            "   - solidPolygonCoordsList: not null\n\n" +
            "   - dashedPolygonCoordsList: **빈 배열일 수 있습니다. not null**\n\n" +
            "       - x: not null\n\n" +
            "       - y: not null\n\n" +
            "   - visibleBounds: not null\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantTierMapDTO.class))})
    })
    @GetMapping("/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @Parameter(schema = @Schema(type = "string"), example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            @CuisineList List<String> cuisines,
            @Parameter(schema = @Schema(type = "string"), example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            @SituationList List<Integer> situations,
            @Parameter(schema = @Schema(type = "string"), example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            @LocationList List<String> locations,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        User user = userService.findUserById(userId);

        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<RestaurantEntity> tieredRestaurants = restaurantTierService.findByConditions(cuisines, situations, locations, 1, false);
        List<RestaurantEntity> nonTieredRestaurants = restaurantTierService.findByConditions(cuisines, situations, locations, -1, false);

        // 2. 상황으로 필터링하기
        List<RestaurantTierDTO> tieredRestaurantTierDTOs = tieredRestaurants.stream().map(restaurant ->
                RestaurantTierDTO.convertRestaurantToTierDTO(
                        restaurant, null, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)))
                .toList();
        List<RestaurantTierDTO> nonTieredRestaurantTierDTOs = nonTieredRestaurants.stream().map(restaurant ->
                        RestaurantTierDTO.convertRestaurantToTierDTO(
                                restaurant, null, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)))
                .toList();

        // 3. 응답 생성하기
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();
        // 3.1 즐겨찾기 리스트
        response.setFavoriteRestaurants(restaurantFavoriteService.getFavoriteRestaurantDtoList(userId));
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
        if (locations != null && !locations.contains(("ALL"))) {
            try {
                for (int i = 0; i < MapConstants.LIST_OF_COORD_LIST.size(); i++) {
                    LocationEnum locationEnum = LocationEnum.valueOf("L" + (i + 1));
                    if (locations.contains(locationEnum.getValue())) {
                        response.getSolidPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    } else {
                        response.getDashedPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
            }
        } else {
            response.setSolidPolygonCoordsList(MapConstants.LIST_OF_COORD_LIST);
        }
        // 3.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(MapConstants.findMinMaxCoordinates(response.getSolidPolygonCoordsList()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "지도 식당 정보 불러오기 (Auth 버전)", description = "[설명]" +
            "\n\n현재 선택한 카테고리에 맞는 전체 식당 정보를 불러옵니다. (현재는 카테고리와 상관없이 고정된 정보를 반환합니다.) " +
            "\n\n티어가 있는 식당(tieredRestaurants)은 zoom에 상관없이 항상 보입니다. " +
            "\n\n티어가 없는 식당은 zoom에 따라 보이는게 달라집니다. " +
            "\n\n티어가 없는 식당(nonTieredRestaurants)은 zoom과 식당 리스트가 함께 주어집니다. " +
            "\n\n현재 지도의 zoom이 17이라면 zoom이 17보다 같거나 작은(.., 16, 17) 식당 리스트를 지도에 표시하시면 됩니다." +
            "\n\n즐겨찾기 리스트에 있는 식당은 지도에서 즐겨찾기 마커로 보여야합니다. (티어 마커나 다른 마커가 아닌 즐겨찾기 마커로 보여야함)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - minZoom: not null\n\n" +
            "   - favoriteRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "   - tieredRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "   - nonTieredRestaurants: **빈 배열일 수 있습니다. not null**\n\n" +
            "       - restaurantId: not null\n\n" +
            "       - restaurantRanking: **null일 수 있습니다.**\n\n" +
            "       - restaurantName: not null\n\n" +
            "       - restaurantCuisine: not null\n\n" +
            "       - restaurantPosition: not null\n\n" +
            "       - restaurantImgUrl: not null\n\n" +
            "       - mainTier: not null\n\n" +
            "       - isEvaluated: not null\n\n" +
            "       - isFavorite: not null\n\n" +
            "       - x: not null\n\n" +
            "       - y: not null\n\n" +
            "       - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "       - restaurantScore: **null일 수 있습니다.**\n\n" +
            "   - solidPolygonCoordsList: not null\n\n" +
            "   - dashedPolygonCoordsList: **빈 배열일 수 있습니다. not null**\n\n" +
            "       - x: not null\n\n" +
            "       - y: not null\n\n" +
            "   - visibleBounds: not null\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantTierMapDTO.class))})
    })
    @GetMapping("/auth/tier/map")
    public ResponseEntity<RestaurantTierMapDTO> getMapInfoWithAuth(
            @Parameter(schema = @Schema(type = "string"), example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            @CuisineList List<String> cuisines,
            @Parameter(schema = @Schema(type = "string"), example = "1,2 또는 ALL", description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
            @SituationList List<Integer> situations,
            @Parameter(schema = @Schema(type = "string"), example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역")
            @LocationList List<String> locations,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        User user = userService.findUserById(userId);

        // 1. 음식 종류랑 위치로 식당 리스트 가져오기
        List<RestaurantEntity> tieredRestaurants = restaurantTierService.findByConditions(cuisines, situations, locations, 1, false);
        List<RestaurantEntity> nonTieredRestaurants = restaurantTierService.findByConditions(cuisines, situations, locations, -1, false);

        // 2. 상황으로 필터링하기
        List<RestaurantTierDTO> tieredRestaurantTierDTOs = tieredRestaurants.stream().map(restaurant ->
                        RestaurantTierDTO.convertRestaurantToTierDTO(
                                restaurant, null, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)))
                .toList();
        List<RestaurantTierDTO> nonTieredRestaurantTierDTOs = nonTieredRestaurants.stream().map(restaurant ->
                        RestaurantTierDTO.convertRestaurantToTierDTO(
                                restaurant, null, restaurantApiService.isEvaluated(restaurant, user), restaurantApiService.isFavorite(restaurant, user)))
                .toList();

        // 3. 응답 생성하기
        RestaurantTierMapDTO response = new RestaurantTierMapDTO();
        // 3.1 즐겨찾기 리스트
        response.setFavoriteRestaurants(restaurantFavoriteService.getFavoriteRestaurantDtoList(userId));
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
        if (locations != null && !locations.contains(("ALL"))) {
            try {
                for (int i = 0; i < MapConstants.LIST_OF_COORD_LIST.size(); i++) {
                    LocationEnum locationEnum = LocationEnum.valueOf("L" + (i + 1));
                    if (locations.contains(locationEnum.getValue())) {
                        response.getSolidPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    } else {
                        response.getDashedPolygonCoordsList().add(MapConstants.LIST_OF_COORD_LIST.get(i));
                    }
                }
            } catch (NumberFormatException e) {
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
            }
        } else {
            response.setSolidPolygonCoordsList(MapConstants.LIST_OF_COORD_LIST);
        }
        // 3.5 지도에 보여야 하는 좌표 범위
        response.setVisibleBounds(MapConstants.findMinMaxCoordinates(response.getSolidPolygonCoordsList()));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
