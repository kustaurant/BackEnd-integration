package com.kustaurant.kustaurant.restaurant.restaurant.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.restaurant.restaurant.service.dto.RestaurantDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface RestaurantApiDoc {

    @Operation(
            summary = "식당 상세 화면 정보 불러오기 (로그인/비로그인 공용),(조회수 기능 포함)",
            description = """
        식당 하나에 대한 상세 정보가 반환됩니다. 
        (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)

        **로그인 상태**: 인증 정보가 있으면 사용자 컨텍스트로 동작합니다.
        **비로그인 상태(앱)**: 비로그인 사용자는 `X-Device-Id` 헤더로 기기 식별자를 보내주세요.
        헤더가 없으면 서버는 새 익명 ID를 발급하여 `X-Anonymous-Id` 응답 헤더로 내려줍니다.
        클라이언트는 저장 후 다음부터 `X-Device-Id`로 전송해 주세요.
        조회수는 사용자 별로 1시간 기준 1회만 증가합니다.

        - 반환 값 보충 설명

          - restaurantId: not null
          - restaurantImgUrl: not null
          - mainTier: not null
          - restaurantCuisine: not null
          - restaurantCuisineImgUrl: not null
          - restaurantPosition: not null
          - restaurantName: not null
          - restaurantAddress: not null
          - isOpen: not null
          - businessHours: not null
          - naverMapUrl: not null
          - situationList: **null이거나 빈 배열일 수 있습니다.**
          - partnershipInfo: **null일 수 있습니다.**
          - evaluationCount: not null
          - restaurantScore: **null일 수 있습니다.**(데이터가 없을 경우)
          - isEvaluated: not null
          - isFavorite: not null
          - favoriteCount: not null
          - restaurantMenuList: **null이거나 빈 배열**이 넘어갈 수 있습니다.
        """,
            parameters = {
                    @Parameter(
                            name = "X-Device-Id",
                            in = ParameterIn.HEADER,
                            required = false,
                            description = "비로그인 앱 식별자. 조회수 증가가 발생하는 호출에서는 반드시 포함",
                            examples = @ExampleObject(name = "예시", value = "a2c2ae2b-4d1a-4b3b-9d27-1b7b2d55a8c9")
                    )
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당 존재함. 응답 정상 반환.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetail.class))}),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 식당이 없음. 또는 폐업함.")
    })
    @GetMapping("/v2/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetail> getRestaurantDetailWithAuth(
            @PathVariable @Parameter(required = true, description = "식당 id", example = "1") Long restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Parameter(hidden = true) HttpServletRequest req,
            @Parameter(hidden = true) HttpServletResponse res
    );


}
