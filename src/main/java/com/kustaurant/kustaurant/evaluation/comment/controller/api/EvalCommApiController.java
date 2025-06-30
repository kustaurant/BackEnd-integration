package com.kustaurant.kustaurant.evaluation.comment.controller.api;

import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommResponse;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.service.EvaluationCommentService;
import com.kustaurant.kustaurant.evaluation.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.evaluation.report.RestaurantCommentReportEntity;
import com.kustaurant.kustaurant.evaluation.report.RestaurantCommentReportRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.RestaurantApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EvalCommApiController {
    private final RestaurantApiService restaurantApiService;
    private final EvaluationCommentService restaurantCommentService;
    private final EvaluationService evaluationService;
    private final EvaluationQueryService evaluationQueryService;
    private final RestaurantCommentReportRepository restaurantCommentReportRepository;


//    // 1. 리뷰 불러오기
//    @Operation(summary = "리뷰 불러오기", description = "인기순 -> sort=popularity \n\n최신순 -> sort=latest\n\n" +
//            "- 반환 값 보충 설명\n\n" +
//            "   - commentId: not null\n\n" +
//            "   - commentScore: not null\n\n" +
//            "   - writerIconImgUrl: not null\n\n" +
//            "   - commentNickname: not null\n\n" +
//            "   - commentTime: not null\n\n" +
//            "   - commentImgUrl: **null일 수 있습니다.**\n\n" +
//            "   - commentBody: **null일 수 있습니다.**\n\n" +
//            "   - commentLikeStatus: not null\n\n" +
//            "   - commentLikeCount: not null\n\n" +
//            "   - commentDislikeCount: not null\n\n" +
//            "   - commentReplies: **null이거나 빈 배열일 수 있습니다.**")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "댓글 리스트입니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = EvalCommResponse.class)))}),
//            @ApiResponse(responseCode = "400", description = "sort 파라미터 입력값이 올바르지 않을 때 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
//    })
//    @GetMapping("/restaurants/{restaurantId}/comments")
//    public ResponseEntity<List<EvalCommResponse>> getReviewList(
//            @PathVariable Integer restaurantId,
//            @RequestParam(defaultValue = "popularity")
//            @Parameter(example = "popularity 또는 latest", description = "인기순: popularity, 최신순: latest") String sort,
//            @Parameter(hidden = true) @AuthUser AuthUserInfo user
//    ) {
//        // sort 파라미터 체크
//        if (!sort.equals("popularity") && !sort.equals("latest")) {
//            throw new ParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
//        }
//        // 식당 가져오기
//        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
//        // 이 식당의 댓글 리스트 가져오기
//        List<EvalCommResponse> response = restaurantCommentService.getRestaurantCommentList(restaurantId, user.id(), sort.equals("popularity"));
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }


    // TODO : 수정해야함 by 경보
    // 2. 리뷰 추천하기
    @Operation(summary = "리뷰 추천하기", description = "추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n반환 형식은 리뷰와 동일합니다." +
            "\n\n추천과 비추천은 동시에 눌릴 수 없고, 비추천을 누른 상태로 추천을 누르면 자동으로 비추천이 해제되고 추천이 누른 상태가 됩니다." +
            "\n\ncommentLikeStatus가 1이면 현재 추천을 누른 상태, -1이면 비추천을 누른 상태, 0이면 아무것도 누르지 않은 상태입니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: **null입니다.**\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - writerIconImgUrl: **null입니다.**\n\n" +
            "   - commentNickname: **null입니다.**\n\n" +
            "   - commentTime: **null입니다.**\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: **null입니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 추천하기 누르고 난 후의 추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvalCommResponse.class))}),
            @ApiResponse(responseCode = "400", description = "해당 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없거나 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "없는 경우겠지만 만에 하나 DB 일관성에 문제가 생겼을 경우 500을 반환하게 했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/like")
    public ResponseEntity<EvalCommReactionResponse> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 평가 댓글인 경우 / 대댓글인 경우 판단
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurantId, commentId);

            EvaluationEntity evaluation = evaluationService.getByEvaluationId(commentId);
            evaluationService.likeEvaluation(user.id(), evaluation);

            return ResponseEntity.ok(new EvalCommReactionResponse(1,1,1,1));
        } else { // 대댓글인 경우
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 코멘트 가져오기
            RestaurantCommentEntity restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 대댓글 좋아요 로직
            Map<String, String> responseMap = new HashMap<>();
            restaurantCommentService.likeComment(user.id(), restaurantComment, responseMap);

            return ResponseEntity.ok(new EvalCommReactionResponse(1,1,1,1));
        }
    }


    // TODO : 수정해야함 by 경보
    // 3. 리뷰 비추천하기
    @Operation(summary = "리뷰 비추천하기", description = "비추천을 누른 후의 추천수와 비추천수를 반환합니다.\n\n" +
            "반환 형식은 댓글과 동일합니다.\n\n추천과 비추천은 동시에 눌릴 수 없고, 추천을 누른 상태로 비추천을 누르면 자동으로 추천이 해제되고 비추천이 누른 상태가 됩니다.\n\n" +
            "commentLikeStatus가 1이면 현재 추천을 누른 상태, -1이면 비추천을 누른 상태, 0이면 아무것도 누르지 않은 상태입니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: **null입니다.**\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - writerIconImgUrl: **null입니다.**\n\n" +
            "   - commentNickname: **null입니다.**\n\n" +
            "   - commentTime: **null입니다.**\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: **null입니다.**\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 비추천하기 누르고 난 후의 비추천 수를 반환해줍니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvalCommResponse.class))}),
            @ApiResponse(responseCode = "400", description = "해당 식당에 해당 commentId를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없거나 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "없는 경우겠지만 만에 하나 DB 일관성에 문제가 생겼을 경우 500을 반환하게 했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/dislike")
    public ResponseEntity<EvalCommReactionResponse> dislikeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 평가 댓글인 경우 / 대댓글인 경우 판단
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurantId, commentId);

            // 평가 가져오기
            EvaluationEntity evaluation = evaluationService.getByEvaluationId(commentId);
            // 댓글 좋아요 로직
            evaluationService.dislikeEvaluation(user.id(), evaluation);

            return ResponseEntity.ok(new EvalCommReactionResponse(1,1,1,1));
        } else { // 대댓글인 경우
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 코멘트 가져오기
            RestaurantCommentEntity restaurantComment = restaurantCommentService.findCommentByCommentId(commentId);
            // 대댓글 좋아요 로직
            Map<String, String> responseMap = new HashMap<>();
            restaurantCommentService.dislikeComment(user.id(), restaurantComment, responseMap);

            return ResponseEntity.ok(new EvalCommReactionResponse(1,1,1,1));
        }
    }


    // TODO : 수정해야함 by 경보
    // 4. 리뷰 대댓글 달기
    @Operation(summary = "리뷰 대댓글 달기", description = "작성한 대댓글을 반환합니다.\n\n" +
            "- 반환 값 보충 설명\n\n" +
            "   - commentId: not null\n\n" +
            "   - commentScore: **null입니다.**\n\n" +
            "   - writerIconImgUrl: not null\n\n" +
            "   - commentNickname: not null\n\n" +
            "   - commentTime: not null\n\n" +
            "   - commentImgUrl: **null입니다.**\n\n" +
            "   - commentBody: not null\n\n" +
            "   - commentLikeStatus: not null\n\n" +
            "   - commentLikeCount: not null\n\n" +
            "   - commentDislikeCount: not null\n\n" +
            "   - commentReplies: **null입니다.**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성한 대댓글을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvalCommResponse.class))}),
            @ApiResponse(responseCode = "400", description = "댓글 내용이 없거나 공백류만 있는 경우와, 해당 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    public ResponseEntity<Void> postReply(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @RequestBody String commentBody,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
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
        checkRestaurantIdAndEvaluationId(restaurantId, evaluationId);
        // Evaluation 가져오기
        EvaluationEntity evaluation = evaluationService.getByEvaluationId(evaluationId);
        // 대댓글 달기
        RestaurantCommentEntity restaurantComment = restaurantCommentService.addSubComment(restaurant, user.id(), commentBody, evaluation);

//        return new ResponseEntity<>(EvalCommResponse.convertCommentWhenSubComment(restaurantComment, null, user.id()), HttpStatus.OK);
        return ResponseEntity.ok().build();
    }

    private void checkRestaurantIdAndEvaluationId(int restaurantId, int evaluationId) {
        if (!evaluationQueryService.hasEvaluation(restaurantId, evaluationId)) {
            throw new ParamException(restaurantId + " 식당에는 " + evaluationId + " id를 가진 evaluation이 없습니다.");
        }
    }

    private void checkRestaurantIdAndCommentId(RestaurantEntity restaurant, int restaurantId, int commentId) {
        if (restaurant.getRestaurantCommentList().stream().noneMatch(comment -> comment.getCommentId().equals(commentId))) {
            throw new ParamException(restaurantId + " 식당에는 " + commentId + " id를 가진 comment가 없습니다.");
        }
    }


    // 5. 리뷰 댓글 및 대댓글 삭제하기

    @Operation(summary = "리뷰 댓글 및 대댓글 삭제하기", description = "리뷰 댓글 및 대댓글 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제에 성공했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @DeleteMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 대댓글인지 평가 코멘트 댓글인지 판단
        if (isSubComment(commentId)) { // 대댓글인 경우
            // 이 댓글이 해당 식당의 댓글이 맞는지 확인
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 댓글 가져오기
            RestaurantCommentEntity comment = restaurantCommentService.findCommentByCommentId(commentId);
            // 댓글 삭제
            restaurantCommentService.deleteComment(comment, user.id());
        } else { // 평가 코멘트 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            checkRestaurantIdAndEvaluationId(restaurantId, commentId);
            // 평가 가져오기
            EvaluationEntity evaluation = evaluationService.getByEvaluationId(commentId);
            // 평가 및 평가의 대댓글 삭제
            evaluationService.deleteComment(evaluation, user.id());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    // 6. 리뷰 댓글 신고하기

    @Operation(summary = "리뷰 댓글 신고하기", description = "신고하기를 누르면 \"정말 신고하시겠습니까?\" 다이얼로그를 띄우고, 이 api를 호출하시면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고에 성공했습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.\n\n또한 commentId에 해당하는 comment가 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    })
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 식당 가져오기
        RestaurantEntity restaurant = restaurantApiService.findRestaurantById(restaurantId);
        // 대댓글인지 평가 코멘트 댓글인지 판단
        if (isSubComment(commentId)) { // 대댓글인 경우
            // 이 댓글이 해당 식당의 댓글이 맞는지 확인
            checkRestaurantIdAndCommentId(restaurant, restaurantId, commentId);
            // 댓글 가져오기
            RestaurantCommentEntity comment = restaurantCommentService.findCommentByCommentId(commentId);
            // 신고 테이블에 저장
            restaurantCommentReportRepository.save(new RestaurantCommentReportEntity(user.id(), comment, LocalDateTime.now(), "ACTIVE"));
        } else { // 평가 코멘트 댓글인 경우
            commentId -= EvaluationConstants.EVALUATION_ID_OFFSET;
            // 이 평가가 해당 식당의 평가가 맞는지 확인
            checkRestaurantIdAndEvaluationId(restaurantId, commentId);
            // 평가 가져오기
            EvaluationEntity evaluation = evaluationService.getByEvaluationId(commentId);
            // 신고 테이블에 저장
            restaurantCommentReportRepository.save(new RestaurantCommentReportEntity(user.id(), evaluation, LocalDateTime.now(), "ACTIVE"));
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private boolean isSubComment(Integer id) {
        return id <= EvaluationConstants.EVALUATION_ID_OFFSET;
    }
}
