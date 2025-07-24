package com.kustaurant.kustaurant.restaurant.draw.controller;

import com.kustaurant.kustaurant.restaurant.draw.service.RestaurantDrawService;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.restaurant.tier.controller.argument_resolver.CuisineList;
import com.kustaurant.kustaurant.restaurant.tier.controller.argument_resolver.LocationList;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class DrawApiController {

    private final RestaurantDrawService drawService;

    @Operation(summary = "뽑기 버튼 클릭 시 조건에 맞는 식당 리스트 반환", description = "위치와 음식 종류에 맞는 식당 중 랜덤으로 30개를 추출하여 반환합니다. 이 30개 중에서 하나를 뽑는 방식으로 뽑기가 진행되고 뽑는 방식은 클라이언트에서 진행됩니다. 조건이 맞는 식당이 30개가 안될 경우 조건에 맞는 식당을 중복으로 추가하여 30개 수량을 맞추고 랜덤으로 섞어서 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 조건에 맞는 맛집이 성공적으로 반환되었습니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantTierDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "해당 조건에 맞는 맛집이 존재하지 않습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) )})
    })
    @GetMapping("/api/v1/draw")
    public ResponseEntity<Object> getRestaurantListForCuisine(
            @Parameter(schema = @Schema(type = "string"), example = "KO,WE,AS 또는 ALL 또는 JH", description = "음식 종류입니다. ALL(전체)과 JH(제휴업체)를 제외하고 복수 선택 가능(콤마로 구분). ALL과 JH가 동시에 포함될 수 없고, ALL이나 JH가 포함되어 있으면 나머지 카테고리는 무시하고 ALL이나 JH를 보여줍니다. (ALL:전체, KO:한식, JA:일식, CH:중식, WE:양식, AS:아시안, ME:고기, CK:치킨, SE:해산물, HP:햄버거/피자, BS:분식, PU:술집, CA:카페/디저트, BA:베이커리, SA:샐러드, JH:제휴업체)")
            @CuisineList List<String> cuisines,
            @Parameter(schema = @Schema(type = "string"), example = "L1,L2,L3 또는 ALL", description = "위치입니다. ALL(전체)을 제외하고 복수 선택 가능(콤마로 구분). ALL이 포함되어 있으면 나머지 카테고리는 무시합니다. (ALL:전체, L1:건입~중문, L2:중문~어대, L3:후문, L4:정문, L5:구의역)")
            @LocationList List<String> locations
    ) {
        return new ResponseEntity<>(drawService.draw(cuisines, locations), HttpStatus.OK);
    }

}
