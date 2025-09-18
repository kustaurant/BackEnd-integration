package com.kustaurant.kustaurant.v1.restaurant;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.ChartCond;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.draw.RestaurantDrawService;
import com.kustaurant.kustaurant.restaurant.query.home.HomeBannerApiService;
import com.kustaurant.kustaurant.restaurant.query.home.RestaurantHomeService;
import com.kustaurant.kustaurant.restaurant.query.search.RestaurantSearchService;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantListsResponse;
import com.kustaurant.kustaurant.v1.restaurant.response.RestaurantTierDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeV1Controller {

    private final HomeBannerApiService homeBannerApiService;
    private final RestaurantHomeService restaurantHomeService;
    private final RestaurantSearchService restaurantSearchService;
    private final RestaurantDrawService drawService;

    @GetMapping("/api/v1/home")
    public ResponseEntity<RestaurantListsResponse> home(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        List<RestaurantCoreInfoDto> topRestaurantsByRatingDTOs = restaurantHomeService.getTopRestaurants(
                user.id()); // 점수 높은 순으로 총 16개
        // 로그인 여부에 따라 랜덤 식당 또는 추천 식당을 반환하는 서비스 메서드를 호출합니다.
        List<RestaurantCoreInfoDto> restaurantsForMeDTOs = restaurantHomeService.getRecommendedOrRandomRestaurants(user.id());
        // 홈화면의 배너 이미지
        List<String> homePhotoUrls = homeBannerApiService.getHomeBannerImage();
        com.kustaurant.kustaurant.restaurant.query.home.RestaurantListsResponse response =
                new com.kustaurant.kustaurant.restaurant.query.home.RestaurantListsResponse(
                        topRestaurantsByRatingDTOs,
                        restaurantsForMeDTOs,
                        homePhotoUrls
                );
        return ResponseEntity.ok(
                RestaurantListsResponse.fromV2(response));
    }

    @GetMapping("/api/v1/search")
    public ResponseEntity<List<RestaurantTierDTO>> search(
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        if (kw == null || kw.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        String[] kwList = kw.split(" ");

        return ResponseEntity.ok(restaurantSearchService
                .search(kwList, user.id()).stream().map(RestaurantTierDTO::fromV2).toList());
    }

    @Operation(
            summary = "뽑기 버튼 클릭 시 조건에 맞는 식당 리스트 반환",
            description = "위치와 음식 종류에 맞는 식당 중 랜덤으로 30개를 추출하여 반환합니다. 이 30개 중에서 하나를 뽑는 방식으로 뽑기가 진행되고 뽑는 방식은 클라이언트에서 진행됩니다. 조건이 맞는 식당이 30개가 안될 경우 조건에 맞는 식당을 중복으로 추가하여 30개 수량을 맞추고 랜덤으로 섞어서 반환합니다.",
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
                            name = "locations", in = ParameterIn.QUERY,
                            description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역",
                            example = "L1,L2,L3",
                            style = ParameterStyle.FORM, explode = Explode.FALSE,
                            array = @ArraySchema(schema = @Schema(type = "string"))
                    )
            }
    )
    @GetMapping("/api/v1/draw")
    public ResponseEntity<List<RestaurantTierDTO>> getRestaurantListForCuisine(
            @Parameter(hidden = true) @ChartCond ChartCondition condition
    ) {
        return new ResponseEntity<>(
                drawService.draw(condition).stream().map(RestaurantTierDTO::fromV2).toList(),
                HttpStatus.OK);
    }
}
