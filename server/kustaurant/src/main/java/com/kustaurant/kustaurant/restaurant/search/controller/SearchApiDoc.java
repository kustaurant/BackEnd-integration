package com.kustaurant.kustaurant.restaurant.search.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.search.service.response.RestaurantSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "restaurant-search-controller")
public interface SearchApiDoc {

    @Operation(summary = "검색하기 V2", description = "리스트 순서대로 출력해주시면 됩니다! 빈 배열인 경우 해당하는 식당이 없다는 화면을 보여주시면 됩니다!\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantRanking: **null입니다.**\n\n" +
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
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantCoreInfoDto.class)))}),
    })
    @GetMapping("/api/v2/search")
    ResponseEntity<List<RestaurantCoreInfoDto>> searchV2(
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );

    @Operation(
            summary = "검색하기 V3",
            description =
                    "검색어에 해당하는 식당 목록을 반환합니다.\n\n" +

                            "### 응답 처리 방법\n" +
                            "- 응답 리스트는 이미 정렬된 상태이므로 그대로 화면에 출력하면 됩니다.\n" +
                            "- 결과 배열이 빈 경우(`[]`) 검색 결과가 없는 상태이므로 '검색 결과 없음' 화면을 표시하면 됩니다.\n\n" +

                            "### 페이징\n" +
                            "- `hasNext`가 `true`인 경우 다음 페이지가 존재합니다.\n" +
                            "- `hasNext`가 `false`인 경우 마지막 페이지입니다.\n\n" +

                            "### 검색 매칭 정보\n" +
                            "각 검색 결과 항목에는 검색어가 어떤 필드에서 매칭되었는지 `matchedFields`로 제공됩니다.\n" +
                            "`matchedFields`에는 다음 값 중 하나 이상이 포함됩니다.\n" +
                            "- `name` : 식당 이름\n" +
                            "- `category` : 식당 카테고리\n" +
                            "- `menu` : 메뉴 이름\n\n" +

                            "### 하이라이트 정보\n" +
                            "- `name` 또는 `category`에서 검색어가 매칭된 경우 해당 문자열에서 매칭된 위치가 `start`, `end` 인덱스로 제공됩니다.\n" +
                            "- `start`부터 `end` **이전까지**의 범위를 하이라이트하면 됩니다. (Java substring 규칙과 동일)\n\n" +

                            "### 메뉴 매칭\n" +
                            "- 메뉴에서 검색어가 매칭된 경우 `matchedMenus`에 매칭된 메뉴 이름이 제공됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantSearchResponse.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
    })
    @GetMapping("/api/v3/search")
    ResponseEntity<RestaurantSearchResponse> searchV3(
            @NotBlank @RequestParam(value = "kw", defaultValue = "") String kw,
            @Parameter(description = "페이지는 1부터 시작하고 디폴트는 1입니다.") @Min(1) @RequestParam(defaultValue = "1", required = false) int page,
            @Parameter(description = "size 디폴트는 30입니다.") @Min(1) @RequestParam(defaultValue = "30", required = false) int size,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );

    @Operation(
            summary = "(2026.04.03) 검색하기 V4",
            description =
                    "검색어에 해당하는 식당 목록을 반환합니다.\n\n" +

                            "### 응답 처리 방법\n" +
                            "- 응답 리스트는 이미 정렬된 상태이므로 그대로 화면에 출력하면 됩니다.\n" +
                            "- 결과 배열이 빈 경우(`[]`) 검색 결과가 없는 상태이므로 '검색 결과 없음' 화면을 표시하면 됩니다.\n\n" +

                            "### 페이징\n" +
                            "- `hasNext`가 `true`인 경우 다음 페이지가 존재합니다.\n" +
                            "- `hasNext`가 `false`인 경우 마지막 페이지입니다.\n\n" +

                            "### 검색 매칭 정보\n" +
                            "각 검색 결과 항목에는 검색어가 어떤 필드에서 매칭되었는지 `matchedFields`로 제공됩니다.\n" +
                            "`matchedFields`에는 다음 값 중 하나 이상이 포함됩니다.\n" +
                            "- `name` : 식당 이름\n" +
                            "- `category` : 식당 카테고리\n" +
                            "- `menu` : 메뉴 이름\n\n" +

                            "### 하이라이트 정보\n" +
                            "- `name` 또는 `category`에서 검색어가 매칭된 경우 해당 문자열에서 매칭된 위치가 `start`, `end` 인덱스로 제공됩니다.\n" +
                            "- `start`부터 `end` **이전까지**의 범위를 하이라이트하면 됩니다. (Java substring 규칙과 동일)\n\n" +

                            "### 메뉴 매칭\n" +
                            "- 메뉴에서 검색어가 매칭된 경우 `matchedMenus`에 매칭된 메뉴 이름이 제공됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청,응답 좋음", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantSearchResponse.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
    })
    @GetMapping("/api/v4/search")
    ResponseEntity<RestaurantSearchResponse> searchV4(
            @NotBlank @RequestParam(value = "kw", defaultValue = "") String kw,
            @Parameter(description = "페이지는 1부터 시작하고 디폴트는 1입니다.") @Min(1) @RequestParam(defaultValue = "1", required = false) int page,
            @Parameter(description = "size 디폴트는 30입니다.") @Min(1) @RequestParam(defaultValue = "30", required = false) int size,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    );
}
