package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.dto.EvaluationDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantDetailDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;


/**
 * @author Ding
 * @since 2024.7.10.
 * description: restaurant controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/restaurants")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters provided", content = {@Content(mediaType = "application/json")})
})
public class RestaurantApiController {
    private final RestaurantApiRepository restaurantApiRepository;

    @Operation(summary = "식당 상세 화면 정보 불러오기", description = "식당 하나에 대한 상세 정보가 반환됩니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당 존재함. 응답 정상 반환.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 식당이 없음. 또는 폐업함.", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetail(
            @PathVariable @Parameter(required = true, description = "식당 id", example = "1") Integer restaurantId
    ) {
        Optional<Restaurant> restaurantOptional = restaurantApiRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) { // 없을 경우 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!restaurantOptional.get().getStatus().equals("ACTIVE")) { // status가 ACTIVE가 아닐 경우 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Restaurant restaurant = restaurantOptional.get();
        DecimalFormat df = new DecimalFormat("#.0");

        RestaurantDetailDTO responseData = RestaurantDetailDTO.convertRestaurantToDetailDTO(restaurant, TierApiController.randomBoolean(), TierApiController.randomBoolean());

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // 즐겨찾기
    @PostMapping("/{restaurantId}/favorite-toggle")
    @Operation(summary = "(기능 작동x) 즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태를 반환합니다.\n\n눌러서 즐겨찾기가 해제된 경우 -> false반환")
    @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Integer restaurantId
    ) {
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    // 이전 평가 데이터 가져오기
    @GetMapping("/{restaurantId}/evaluation")
    @Operation(summary = "(기능 작동x) 평가 하기로 갈 때 이전 평가 데이터가 있을 경우 불러오기", description = "평가하기에서 사용하는 형식과 동일합니다. 유저가 이전에 해당 식당을 평가했을 경우 이전 평가 데이터를 불러와서 이전에 평가했던 사항을 보여줍니다.")
    @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationDTO.class))})
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Integer restaurantId
    ) {
        EvaluationDTO evaluationData = new EvaluationDTO(
                4.5d,
                List.of("혼밥", "데이트"),
                "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyNDA3MDlfMTI0%2FMDAxNzIwNDYzNTMyMDY0.P6NfUsNuS1RZNhOijag4lq8-gKW3DunI95D2B_n2hc4g.7NRJfKXXTyfoQqeZoWWKePG7dQNA5M9tWXnexIxX_k8g.JPEG%2Foutput_3125420507.jpg&type=sc960_832",
                "맛있다~~~~~~~~~!!!!!!!!!!!!!!!"
        );
        return new ResponseEntity<>(evaluationData, HttpStatus.OK);
    }

    // 평가하기
    @PostMapping("/{restaurantId}/evaluation")
    @Operation(summary = "(기능 작동x) 평가하기", description = "평가하기 입니다.")
    @ApiResponse(responseCode = "200", description = "평가하기 후에 식당 정보를 다시 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))})
    public ResponseEntity<RestaurantDetailDTO> evaluateRestaurant(
            @PathVariable Integer restaurantId,
            @RequestBody EvaluationDTO evaluationDTO,
            @RequestParam(required = false) MultipartFile image
            ) {
        Optional<Restaurant> restaurantOptional = restaurantApiRepository.findById(restaurantId);
        if (restaurantOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(RestaurantDetailDTO.convertRestaurantToDetailDTO(restaurantOptional.get(), TierApiController.randomBoolean(), TierApiController.randomBoolean()), HttpStatus.OK);
    }

    // 리뷰 불러오기
    @GetMapping("/{restaurantId}/comments")
    @Operation(summary = "(기능 작동x) 리뷰 불러오기", description = "인기순 -> sort=popularity \n\n최신순 -> sort=latest")
    @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantComment.class)))})
    public ResponseEntity<List<RestaurantComment>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "popularity")
            @Parameter(example = "popularity 또는 latest", description = "인기순: popularity, 최신순: latest")
            String sort
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    // 리뷰 추천하기
    @PostMapping("/{restaurantId}/comments/{commentId}/like")
    @Operation(summary = "(기능 작동x) 리뷰 추천하기", description = "추천을 누른 후의 추천 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 추천하기 누르고 난 후의 추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
    public ResponseEntity<Integer> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        return new ResponseEntity<>(123, HttpStatus.OK);
    }

    // 리뷰 비추천하기
    @PostMapping("/{restaurantId}/comments/{commentId}/dislike")
    @Operation(summary = "(기능 작동x) 리뷰 비추천하기", description = "비추천을 누른 후의 비추천 수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 비추천하기 누르고 난 후의 비추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
    public ResponseEntity<Integer> dislikeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        return new ResponseEntity<>(33, HttpStatus.OK);
    }

    // 식당 대댓글 달기
    @PostMapping("/{restaurantId}/comments/{commentId}")
    @Operation(summary = "(기능 작동x) 식당 대댓글 달기", description = "작성한 대댓글을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "작성한 대댓글을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantComment.class))})
    public ResponseEntity<RestaurantComment> postReply(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @RequestBody String commentBody
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
