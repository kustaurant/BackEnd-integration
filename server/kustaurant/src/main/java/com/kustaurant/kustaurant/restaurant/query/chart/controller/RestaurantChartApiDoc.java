package com.kustaurant.kustaurant.restaurant.query.chart.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.chart.controller.dto.RestaurantChartResponse;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantTierMapDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "restaurant-tier-chart-controller")
public interface RestaurantChartApiDoc {

    @Operation(
            summary = "티어표 리스트 불러오기",
            description = "파라미터로 받는 page(1부터 카운트)의 limit개의 식당 리스트를 반환합니다. 현재는 파라미터와 무관한 데이터를 반환합니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
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
            "   - restaurantScore: **null일 수 있습니다.**",
            parameters = {
                    @Parameter(
                            name = "cuisines", in = ParameterIn.QUERY,
                            description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)",
                            example = "KO,WE,AS 또는 ALL 또는 JH",
                            // CSV 표기
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "situations", in = ParameterIn.QUERY,
                            description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)",
                            example = "1,2 또는 ALL",
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "locations", in = ParameterIn.QUERY,
                            description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역",
                            example = "L1,L2,L3",
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "page", in = ParameterIn.QUERY,
                            description = "페이지 (1부터 시작).",
                            schema = @Schema(type = "integer", defaultValue = "1", minimum = "1")
                    ),
                    @Parameter(
                            name = "limit", in = ParameterIn.QUERY,
                            description = "한 페이지의 항목 개수.",
                            schema = @Schema(type = "integer", defaultValue = "30", minimum = "1", maximum = "100")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantCoreInfoDto.class)))}),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 잘못됨.")
    })
    @GetMapping(value = "/v2/tier")
    ResponseEntity<RestaurantChartResponse> getTierChartList(
            @Parameter(hidden = true) @ChartCond ChartCondition condition,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );

    @Operation(
            summary = "지도 식당 정보 불러오기",
            description = "[설명]" +
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
            "   - visibleBounds: not null\n\n",
            parameters = {
                    @Parameter(
                            name = "cuisines", in = ParameterIn.QUERY,
                            description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)",
                            example = "KO,WE,AS 또는 ALL 또는 JH",
                            // CSV 표기
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "situations", in = ParameterIn.QUERY,
                            description = "상황입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, 1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)",
                            example = "1,2 또는 ALL",
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    ),
                    @Parameter(
                            name = "locations", in = ParameterIn.QUERY,
                            description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역",
                            example = "L1,L2,L3",
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantTierMapDTO.class))})
    })
    @GetMapping("/v2/tier/map")
    ResponseEntity<RestaurantTierMapDTO> getMapInfo(
            @Parameter(hidden = true) @ChartCond ChartCondition condition,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );
}
