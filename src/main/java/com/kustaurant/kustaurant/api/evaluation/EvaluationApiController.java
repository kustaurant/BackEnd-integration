package com.kustaurant.kustaurant.api.evaluation;

import com.kustaurant.kustaurant.api.restaurant.service.RestaurantApiService;
import com.kustaurant.kustaurant.common.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.common.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantComment;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantCommentReport;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantCommentReportRepository;
import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantCommentDTO;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantCommentService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantFavoriteService;
import com.kustaurant.kustaurant.common.restaurant.service.RestaurantService;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.global.UserService;
import com.kustaurant.kustaurant.global.apiUser.customAnno.JwtToken;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EvaluationApiController {

    private final RestaurantService restaurantService;

    private final UserService userService;
    private final RestaurantApiService restaurantApiService;
    private final RestaurantFavoriteService restaurantFavoriteService;
    private final RestaurantCommentService restaurantCommentService;
    private final EvaluationService evaluationService;

    private final RestaurantCommentReportRepository restaurantCommentReportRepository;

    // 이전 평가 데이터 가져오기
    @GetMapping("/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "평가 하기로 갈 때 이전 평가 데이터가 있을 경우 불러오기", description = "평가하기에서 사용하는 형식과 동일합니다. 유저가 이전에 해당 식당을 평가했을 경우 이전 평가 데이터를 불러와서 이전에 평가했던 사항을 보여줍니다. " +
            "\n\n이전 데이터가 없을 경우 아무것도 반환하지 않습니다.\n\n현재 restaurantId 599, 631에 데이터 있습니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - evaluationScore: **null일 수 있습니다.**\n\n" +
            "   - evaluationSituations: **null이거나 빈 배열일 수 있습니다.**\n\n" +
            "   - evaluationImgUrl: **null일 수 있습니다.**\n\n" +
            "   - evaluationComment: **null일 수 있습니다.**\n\n" +
            "   - starComment: not null\n\n" +
            "   - newImage: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvaluationDTO.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 해당 식당에 대한 이전 평가가 있을 경우 이전 평가 데이터를 반환해주고, 이전 평가가 없으면 별점 코멘트 데이터만 반환
        return user.getEvaluationList().stream()
                .filter(evaluation -> evaluation.getRestaurant().equals(restaurant) && evaluation.getStatus().equals("ACTIVE"))
                .findFirst()
                .map(evaluation -> {
                    return ResponseEntity.ok(EvaluationDTO.convertEvaluation(evaluation));
                })
                .orElse(ResponseEntity.ok(EvaluationDTO.convertEvaluationWhenNoEvaluation()));
    }

    // 평가하기
    @PostMapping(value = "/auth/restaurants/{restaurantId}/evaluation")
    @Operation(summary = "평가하기", description = "평가하기 입니다.\n\n상황 리스트는 정수 리스트로 ex) [2,3,7] (1:혼밥, 2:2~4인, 3:5인 이상, 4:단체 회식, 5:배달, 6:야식, 7:친구 초대, 8:데이트, 9:소개팅)\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - evaluationScore: 필수\n\n" +
            "   - evaluationSituations: 사용자가 상황을 선택했을 경우만. 없어도 됩니다.\n\n" +
            "   - evaluationImgUrl: 사용 안함. 없어도 됩니다.\n\n" +
            "   - evaluationComment: 사용자가 코멘트를 입력했을 경우만. 없어도 됩니다.\n\n" +
            "   - starComments: 사용 안함. 없어도 됩니다.\n\n" +
            "   - newImage: 사용자가 이미지를 추가했을 경우만. 없어도 됩니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: not null\n\n" +
            "   - commentScore: not null\n\n" +
            "   - commentIconImgUrl: not null\n\n" +
            "   - commentNickname: not null\n\n" +
            "   - commentTime: not null\n\n" +
            "   - commentImgUrl: **null일 수 있습니다.**\n\n" +
            "   - commentBody: **null일 수 있습니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null이거나 빈 배열일 수 있습니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "평가하기 후에 식당 정보를 다시 반환해줍니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RestaurantCommentDTO.class)))}),
            @ApiResponse(responseCode = "400", description = "평가 점수가 없거나 0점인 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "데이터베이스 상에 문제가 있을 경우 500을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<List<RestaurantCommentDTO>> evaluateRestaurant(
            @PathVariable Integer restaurantId,
            EvaluationDTO evaluationDTO,
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 필수 파라미터 체크
        if (evaluationDTO.getEvaluationScore() == null || evaluationDTO.getEvaluationScore().equals(0d)) {
            throw new ParamException("평가 점수가 필요합니다.");
        }
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 평가 추가하기 혹은 기존 평가 업데이트하기
        evaluationService.createOrUpdate(user, restaurant, evaluationDTO);
        // 평가 완료 후에 업데이트된 식당 데이터를 다시 반환
        return new ResponseEntity<>(restaurantCommentService.getRestaurantCommentList(restaurant, user, true, userAgent), HttpStatus.OK);
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
            "   - commentBody: **null일 수 있습니다.**\n\n" +
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
            String sort,
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // sort 파라미터 체크
        if (!sort.equals("popularity") && !sort.equals("latest")) {
            throw new ParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
        }
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유져 가져오기
        User user = userService.findUserById(userId);
        // 이 식당의 댓글 리스트 가져오기
        List<RestaurantCommentDTO> response = restaurantCommentService.getRestaurantCommentList(restaurant, user, sort.equals("popularity"), userAgent);

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
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 평가 댓글인 경우 / 대댓글인 경우 판단
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurant, restaurantId, commentId);

            // 평가 가져오기
            Evaluation evaluation = evaluationService.getByEvaluationId(commentId);
            // 댓글 좋아요 로직
            evaluationService.likeEvaluation(user, evaluation);

            return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(evaluation, user));
        } else { // 대댓글인 경우
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 코멘트 가져오기
            RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 대댓글 좋아요 로직
            Map<String, String> responseMap = new HashMap<>();
            restaurantCommentService.likeComment(user, restaurantComment, responseMap);

            return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(restaurantComment, user));
        }
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
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 평가 댓글인 경우 / 대댓글인 경우 판단
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurant, restaurantId, commentId);

            // 평가 가져오기
            Evaluation evaluation = evaluationService.getByEvaluationId(commentId);
            // 댓글 좋아요 로직
            evaluationService.dislikeEvaluation(user, evaluation);

            return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(evaluation, user));
        } else { // 대댓글인 경우
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 코멘트 가져오기
            RestaurantComment restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 대댓글 좋아요 로직
            Map<String, String> responseMap = new HashMap<>();
            restaurantCommentService.dislikeComment(user, restaurantComment, responseMap);

            return ResponseEntity.ok(RestaurantCommentDTO.convertCommentWhenLikeDislike(restaurantComment, user));
        }
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
            @RequestBody String commentBody,
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        int evaluationId = commentId - EvaluationConstants.EVALUATION_ID_OFFSET;
        // 댓글 내용 없는 경우 예외 처리
        if (commentBody.trim().isEmpty()) {
            throw new ParamException("대댓글 내용이 없습니다.");
        }
        // 댓글 내용 전처리
        commentBody = commentBody.replaceAll("^\"|\"$", ""); // 양쪽 큰 따옴표 제거
        commentBody = commentBody.replace("\\\"", "\"");     // 이스케이프된 따옴표 복구
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 식당에 해당하는 commentId를 갖는 comment가 없는 경우 예외 처리
        checkRestaurantIdAndEvaluationId(restaurant, restaurantId, evaluationId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // Evaluation 가져오기
        Evaluation evaluation = evaluationService.getByEvaluationId(evaluationId);
        // 대댓글 달기
        RestaurantComment restaurantComment = restaurantCommentService.addSubComment(restaurant, user, commentBody, evaluation);

        return new ResponseEntity<>(RestaurantCommentDTO.convertCommentWhenSubComment(restaurantComment, null, user, userAgent), HttpStatus.OK);
    }

    private void checkRestaurantIdAndEvaluationId(RestaurantEntity restaurant, int restaurantId, int evaluationId) {
        if (restaurant.getEvaluationList().stream().noneMatch(evaluation -> evaluation.getEvaluationId().equals(evaluationId))) {
            throw new ParamException(restaurantId + " 식당에는 " + evaluationId + " id를 가진 evaluation이 없습니다.");
        }
    }

    private void checkRestaurantIdAndCommentId(RestaurantEntity restaurant, int restaurantId, int commentId) {
        if (restaurant.getRestaurantCommentList().stream().noneMatch(comment -> comment.getCommentId().equals(commentId))) {
            throw new ParamException(restaurantId + " 식당에는 " + commentId + " id를 가진 comment가 없습니다.");
        }
    }

    // 식당 댓글 및 대댓글 삭제하기
    @DeleteMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    @Operation(summary = "식당 댓글 및 대댓글 삭제하기", description = "식당 댓글 및 대댓글 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 대댓글인지 평가 코멘트 댓글인지 판단
        if (isSubComment(commentId)) { // 대댓글인 경우
            // 이 댓글이 해당 식당의 댓글이 맞는지 확인
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 댓글 가져오기
            RestaurantComment comment = restaurantCommentService.findCommentByCommentId(commentId);
            // 댓글 삭제
            restaurantCommentService.deleteComment(comment, user);
        } else { // 평가 코멘트 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurant, restaurantId, commentId);
            // 평가 가져오기
            Evaluation evaluation = evaluationService.getByEvaluationId(commentId);
            // 평가 및 평가의 대댓글 삭제
            evaluationService.deleteComment(evaluation, user);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 댓글 신고하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/report")
    @Operation(summary = "댓글 신고하기", description = "신고하기를 누르면 \"정말 신고하시겠습니까?\" 다이얼로그를 띄우고, 이 api를 호출하시면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고에 성공했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.\n\n또한 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    public ResponseEntity<Void> reportComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @JwtToken Integer userId
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 유저 가져오기
        User user = userService.findUserById(userId);
        // 대댓글인지 평가 코멘트 댓글인지 판단
        if (isSubComment(commentId)) { // 대댓글인 경우
            // 이 댓글이 해당 식당의 댓글이 맞는지 확인
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 댓글 가져오기
            RestaurantComment comment = restaurantCommentService.findCommentByCommentId(commentId);
            // 신고 테이블에 저장
            restaurantCommentReportRepository.save(new RestaurantCommentReport(user, comment, LocalDateTime.now(), "ACTIVE"));
        } else { // 평가 코멘트 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            // 이 평가가 해당 식당의 평가가 맞는지 확인
            checkRestaurantIdAndEvaluationId(restaurant, restaurantId, commentId);
            // 평가 가져오기
            Evaluation evaluation = evaluationService.getByEvaluationId(commentId);
            // 신고 테이블에 저장
            restaurantCommentReportRepository.save(new RestaurantCommentReport(user, evaluation, LocalDateTime.now(), "ACTIVE"));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isSubComment(Integer id) {
        return id <= EvaluationConstants.EVALUATION_ID_OFFSET;
    }
}
