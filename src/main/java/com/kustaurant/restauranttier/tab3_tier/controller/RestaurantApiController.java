package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.common.exception.ErrorResponse;
import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.dto.EvaluationDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantCommentDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantDetailDTO;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantApiService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantCommentService;
import com.kustaurant.restauranttier.tab3_tier.service.RestaurantFavoriteService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.service.MypageApiService;
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
public class RestaurantApiController {
    private final MypageApiService mypageApiService;
    private final RestaurantApiService restaurantApiService;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantCommentService restaurantCommentService;

    private final int userId = 23;

    @Operation(summary = "식당 상세 화면 정보 불러오기", description = "식당 하나에 대한 상세 정보가 반환됩니다. (mainTier가 -1인 것은 티어가 아직 매겨지지 않은 식당입니다.)\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - restaurantId: not null\n\n" +
            "   - restaurantImgUrl: not null\n\n" +
            "   - mainTier: not null\n\n" +
            "   - restaurantCuisine: not null\n\n" +
            "   - restaurantCuisineImgUrl: not null\n\n" +
            "   - restaurantPosition: not null\n\n" +
            "   - restaurantName: not null\n\n" +
            "   - restaurantAddress: not null\n\n" +
            "   - isOpen: not null\n\n" +
            "   - businessHours: not null\n\n" +
            "   - naverMapUrl: not null\n\n" +
            "   - situationList: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - partnershipInfo: **null일 수 있습니다.**\n\n" +
            "   - evaluationCount: not null\n\n" +
            "   - restaurantScore: **null일 수 있습니다.**(데이터가 없을 경우)\n\n" +
            "   - isEvaluated: not null\n\n" +
            "   - isFavorite: not null\n\n" +
            "   - favoriteCount: not null\n\n" +
            "   - restaurantMenuList: **null이거나 빈 배열**이 넘어갈 수 있습니다.")
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
        User user = mypageApiService.findUserById(userId);
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
    @Operation(summary = "즐겨찾기 추가/해제 토글", description = "즐겨찾기 버튼을 누른 후의 즐겨찾기 상태를 반환합니다.\n\n눌러서 즐겨찾기가 해제된 경우 -> false반환\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - boolean: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "retaurantId에 해당하는 식당이 존재하지 않을 때 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<Boolean> restaurantFavoriteToggle(
            @PathVariable Integer restaurantId
    ) {
        // TODO: 로그인 구현 후 수정
        User user = mypageApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + " 유저가 존재하지 않습니다.");
        }

        boolean result = restaurantFavoriteService.toggleFavorite(user.getNaverProviderId(), restaurantId);
        return ResponseEntity.ok(result);
    }

    // 이전 평가 데이터 가져오기
    @GetMapping("/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "평가 하기로 갈 때 이전 평가 데이터가 있을 경우 불러오기", description = "평가하기에서 사용하는 형식과 동일합니다. 유저가 이전에 해당 식당을 평가했을 경우 이전 평가 데이터를 불러와서 이전에 평가했던 사항을 보여줍니다. " +
            "\n\n이전 데이터가 없을 경우 아무것도 반환하지 않습니다.\n\n현재 restaurantId 599, 631에 데이터 있습니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - evaluationScore: **null일 수 있습니다.**\n\n" +
            "   - evaluationSituations: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - evaluationImgUrl: **null일 수 있습니다.**\n\n" +
            "   - evaluationComment: **null일 수 있습니다.**\n\n" +
            "   - starComment: not null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationDTO.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Integer restaurantId
    ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        User user = mypageApiService.findUserById(userId);
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
                .orElse(ResponseEntity.ok(EvaluationDTO.convertEvaluationWhenNoEvaluation()));
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
    @Operation(summary = "리뷰 불러오기", description = "인기순 -> sort=popularity \n\n최신순 -> sort=latest\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: not null\n\n" +
            "   - commentScore: not null\n\n" +
            "   - commentIconImgUrl: not null\n\n" +
            "   - commentNickname: not null\n\n" +
            "   - commentTime: not null\n\n" +
            "   - commentImgUrl: **null일 수 있습니다.**\n\n" +
            "   - commentBody: not null\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null이거나 빈 배열일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantCommentDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "sort 파라미터 입력값이 올바르지 않을 때 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<List<RestaurantCommentDTO>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "popularity")
            @Parameter(example = "popularity 또는 latest", description = "인기순: popularity, 최신순: latest")
            String sort
    ) {
        if (!sort.equals("popularity") && !sort.equals("latest")) {
            throw new ParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
        }
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // TODO 나중에 수정
        User user = mypageApiService.findUserById(userId);

        List<RestaurantCommentDTO> response = restaurantCommentService.getRestaurantCommentList(restaurant, user, sort.equals("popularity"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 리뷰 추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/like")
    @Operation(summary = "리뷰 추천하기", description = "추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n반환 형식은 리뷰와 동일합니다." +
            "\n\n추천과 비추천은 동시에 눌릴 수 없고, 비추천을 누른 상태로 추천을 누르면 자동으로 비추천이 해제되고 추천이 누른 상태가 됩니다." +
            "\n\ncommentLikeStatus가 1이면 현재 추천을 누른 상태, -1이면 비추천을 누른 상태, 0이면 아무것도 누르지 않은 상태입니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: **null입니다.**\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - commentIconImgUrl: **null입니다.**\n\n" +
            "   - commentNickname: **null입니다.**\n\n" +
            "   - commentTime: **null입니다.**\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: **null입니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 추천하기 누르고 난 후의 추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantCommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "해당 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없거나 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "없는 경우겠지만 만에 하나 DB 일관성에 문제가 생겼을 경우 500을 반환하게 했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<RestaurantCommentDTO> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 식당에 해당하는 commentId를 갖는 comment가 없는 경우 예외 처리
        checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);

        // TODO: 로그인 구현 후 수정
        User user = mypageApiService.findUserById(userId);
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
    @Operation(summary = "리뷰 비추천하기", description = "비추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n" +
            "반환 형식은 댓글과 동일합니다.\n\n추천과 비추천은 동시에 눌릴 수 없고, 추천을 누른 상태로 비추천을 누르면 자동으로 추천이 해제되고 비추천이 누른 상태가 됩니다.\n\n" +
            "commentLikeStatus가 1이면 현재 추천을 누른 상태, -1이면 비추천을 누른 상태, 0이면 아무것도 누르지 않은 상태입니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: **null입니다.**\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - commentIconImgUrl: **null입니다.**\n\n" +
            "   - commentNickname: **null입니다.**\n\n" +
            "   - commentTime: **null입니다.**\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: **null입니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 비추천하기 누르고 난 후의 비추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantCommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "해당 식당에 해당 commentId를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없거나 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "없는 경우겠지만 만에 하나 DB 일관성에 문제가 생겼을 경우 500을 반환하게 했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<RestaurantCommentDTO> dislikeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 식당에 해당하는 commentId를 갖는 comment가 없는 경우 예외 처리
        checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);

        // TODO: 로그인 구현 후 수정
        User user = mypageApiService.findUserById(userId);
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
    @Operation(summary = "식당 대댓글 달기", description = "작성한 대댓글을 반환합니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: not null\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - commentIconImgUrl: not null\n\n" +
            "   - commentNickname: not null\n\n" +
            "   - commentTime: not null\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: not null\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성한 대댓글을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantCommentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "댓글 내용이 없거나 공백류만 있는 경우와, 해당 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<RestaurantCommentDTO> postReply(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @RequestBody String commentBody
    ) {
        // 댓글 내용 없는 경우 예외 처리
        if (commentBody.trim().isEmpty()) {
            throw new ParamException("대댓글 내용이 없습니다.");
        }

        Restaurant restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 식당에 해당하는 commentId를 갖는 comment가 없는 경우 예외 처리
        checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
        // TODO: 로그인 구현 후 수정
        User user = mypageApiService.findUserById(userId);
        if (user == null) {
            throw new OptionalNotExistException(userId + "해당 유저가 없습니다.");
        }

        RestaurantComment restaurantComment = restaurantCommentService.addSubComment(restaurant, user, commentBody, commentId);

        return new ResponseEntity<>(RestaurantCommentDTO.convertComment(restaurantComment, null, null), HttpStatus.OK);
    }

    private void checkRestaurantIdAndCommentId(Restaurant restaurant, int restaurantId, int commentId) {
        if (restaurant.getRestaurantCommentList().stream().noneMatch(comment -> comment.getCommentId().equals(commentId))) {
            throw new ParamException(restaurantId + " 식당에는 " + commentId + " id를 가진 comment가 없습니다.");
        }
    }

    // 식당 댓글 및 대댓글 삭제하기
    @DeleteMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    @Operation(summary = "(기능 작동x) 식당 댓글 및 대댓글 삭제하기", description = "식당 댓글 및 대댓글 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))})
    })
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId
    ) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
