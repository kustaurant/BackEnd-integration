package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.TierParamException;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.dto.EvaluationDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantCommentDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantDetailDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantCommentService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantFavoriteService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.service.UserApiService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Ding
 * @since 2024.7.10.
 * description: restaurant controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid parameters provided", content = {@Content(mediaType = "application/json")})
})
public class RestaurantApiController {
    private final UserApiService userApiService;
    private final RestaurantApiService restaurantApiService;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantCommentService restaurantCommentService;

    private final int userId = 23;

    @Operation(summary = "식당 상세 화면 정보 불러오기", description = "식당 하나에 대한 상세 정보가 반환됩니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식당 존재함. 응답 정상 반환.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 id를 가진 식당이 없음. 또는 폐업함.", content = {@Content(mediaType = "application/json")})
    })
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDetailDTO> getRestaurantDetail(
            @PathVariable @Parameter(required = true, description = "식당 id", example = "1") Integer restaurantId
    ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);

        // TODO: 로그인 구현 후 수정
        User user = userApiService.findUserById(userId);
        boolean isFavorite = false;
        boolean isEvaluated = false;
        if (user != null) {
            isFavorite = restaurantApiService.isFavorite(restaurant, user);
            isEvaluated = restaurantApiService.isEvaluated(restaurant, user);
        }

        RestaurantDetailDTO responseData = RestaurantDetailDTO.convertRestaurantToDetailDTO(restaurant, isEvaluated, isFavorite);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // 즐겨찾기
    @PostMapping("/auth/restaurants/{restaurantId}/favorite-toggle")
    @Operation(summary = "즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태를 반환합니다.\n\n눌러서 즐겨찾기가 해제된 경우 -> false반환")
    @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))})
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Integer restaurantId
    ) {
        // TODO: 로그인 구현 후 수정
        User user = userApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + " 유저가 존재하지 않습니다.");
        }

        boolean result = restaurantFavoriteService.toggleFavorite(user.getNaverProviderId(), restaurantId);
        return ResponseEntity.ok(result);
    }

    // 이전 평가 데이터 가져오기
    @GetMapping("/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "평가 하기로 갈 때 이전 평가 데이터가 있을 경우 불러오기", description = "평가하기에서 사용하는 형식과 동일합니다. 유저가 이전에 해당 식당을 평가했을 경우 이전 평가 데이터를 불러와서 이전에 평가했던 사항을 보여줍니다. " +
            "\n\n이전 데이터가 없을 경우 아무것도 반환하지 않습니다.\n\n현재 restaurantId 599, 631에 데이터 있습니다.")
    @ApiResponse(responseCode = "200", description = "success\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationDTO.class))})
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Integer restaurantId
    ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        User user = userApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + " 유저가 없습니다.");
        }

        return user.getEvaluationList().stream()
                .filter(evaluation -> evaluation.getRestaurant().equals(restaurant))
                .findFirst()
                .map(evaluation -> {
                    RestaurantComment comment = restaurantCommentService.findCommentByEvaluationId(evaluation.getEvaluationId());
                    return ResponseEntity.ok(EvaluationDTO.convertEvaluation(evaluation, comment));
                })
                .orElse(ResponseEntity.ok(null));
    }

    // 평가하기
    @PostMapping("/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "(기능 작동x) 평가하기", description = "평가하기 입니다.\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)")
    @ApiResponse(responseCode = "200", description = "평가하기 후에 식당 정보를 다시 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDetailDTO.class))})
    public ResponseEntity<RestaurantDetailDTO> evaluateRestaurant(
            @PathVariable Integer restaurantId,
            @RequestBody EvaluationDTO evaluationDTO,
            @RequestParam(required = false) MultipartFile image
            ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);

        return new ResponseEntity<>(RestaurantDetailDTO.convertRestaurantToDetailDTO(restaurant, TierApiController.randomBoolean(), TierApiController.randomBoolean()), HttpStatus.OK);
    }

    // 리뷰 불러오기
    @GetMapping("/restaurants/{restaurantId}/comments")
    @Operation(summary = "리뷰 불러오기", description = "인기순 -> sort=popularity \n\n최신순 -> sort=latest")
    @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantCommentDTO.class)))})
    public ResponseEntity<List<RestaurantCommentDTO>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "popularity")
            @Parameter(example = "popularity 또는 latest", description = "인기순: popularity, 최신순: latest")
            String sort
    ) {
        if (!sort.equals("popularity") && !sort.equals("latest")) {
            throw new TierParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
        }
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // TODO 나중에 수정
        User user = userApiService.findUserById(userId);

        List<RestaurantCommentDTO> response = restaurantCommentService.getRestaurantCommentList(restaurant, user, sort.equals("popularity"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 리뷰 추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/like")
    @Operation(summary = "(기능 작동x) 리뷰 추천하기", description = "추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n반환 형식은 리뷰와 동일합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 추천하기 누르고 난 후의 추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
    public ResponseEntity<RestaurantCommentDTO> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        // TODO: 로그인 구현 후 수정
        User user = userApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + "해당 유저가 없습니다.");
        }

        RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);

        Map<String, String> responseMap = new HashMap<>();
        restaurantCommentService.likeComment(user, restaurantComment, responseMap);

        return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(restaurantComment, user));
    }

    // 리뷰 비추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/dislike")
    @Operation(summary = "리뷰 비추천하기", description = "비추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n반환 형식은 댓글과 동일합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 비추천하기 누르고 난 후의 비추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class))})
    public ResponseEntity<RestaurantCommentDTO> dislikeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        // TODO: 로그인 구현 후 수정
        User user = userApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + "해당 유저가 없습니다.");
        }

        RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);

        Map<String, String> responseMap = new HashMap<>();
        restaurantCommentService.dislikeComment(user, restaurantComment, responseMap);

        return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(restaurantComment, user));
    }

    // 식당 대댓글 달기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    @Operation(summary = "식당 대댓글 달기", description = "작성한 대댓글의 부모 댓글을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "작성한 대댓글의 부모 댓글을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantCommentDTO.class))})
    public ResponseEntity<RestaurantCommentDTO> postReply(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @RequestBody String commentBody
    ) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

}
