package com.kustaurant.mainapp.v1.evaluation;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.common.enums.SortOption;
import com.kustaurant.mainapp.evaluation.comment.controller.port.EvalCommCommandService;
import com.kustaurant.mainapp.evaluation.comment.controller.port.EvalCommentReactionService;
import com.kustaurant.mainapp.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;
import com.kustaurant.mainapp.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.mainapp.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.mainapp.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.mainapp.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.mainapp.evaluation.evaluation.service.EvaluationReactionService;
import com.kustaurant.mainapp.evaluation.review.ReviewQueryService;
import com.kustaurant.mainapp.evaluation.review.ReviewsResponse;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.ApiStatusException;
import com.kustaurant.mainapp.global.exception.exception.ParamException;
import com.kustaurant.mainapp.user.user.controller.port.UserService;
import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.v1.evaluation.response.RestaurantCommentDTO;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EvaluationV1Controller {

    public static final int EVALUATION_ID_OFFSET = 10000000;

    private final ReviewQueryService reviewQueryService;
    private final EvalCommCommandService evalCommCommandService;
    private final UserService userService;
    private final EvaluationQueryService evaluationQueryService;
    private final EvaluationCommandService evaluationCommandService;
    private final EvaluationReactionService evaluationReactionService;
    private final EvalCommentReactionService evalCommUserReactionService;

    // 이전 평가 데이터 가져오기
    @GetMapping("/auth/restaurants/{restaurantId}/evaluation")
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Integer restaurantId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        EvaluationDTO dto = evaluationQueryService.getPreEvaluation(user.id(), (long) restaurantId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    // 평가하기
    @PostMapping(value = "/auth/restaurants/{restaurantId}/evaluation")
    public ResponseEntity<List<RestaurantCommentDTO>> evaluateRestaurant(
            @PathVariable Integer restaurantId,
            EvaluationDTO evaluationDTO,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 필수 파라미터 체크
        if (evaluationDTO.getEvaluationScore() == null || evaluationDTO.getEvaluationScore().equals(0d)) {
            throw new ParamException("평가 점수가 필요합니다.");
        }

        // 평가 데이터 저장 또는 업데이트
        evaluationCommandService.evaluate(user.id(), (long) restaurantId, evaluationDTO);

        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments((long) restaurantId, user.id(), SortOption.POPULARITY);

        return new ResponseEntity<>(response.stream().map(RestaurantCommentDTO::fromV2).toList(), HttpStatus.OK);
    }

    // 리뷰 불러오기
    @GetMapping("/restaurants/{restaurantId}/comments")
    public ResponseEntity<List<RestaurantCommentDTO>> getReviewList(
            @PathVariable Integer restaurantId,
            @RequestParam(defaultValue = "popularity")
            String sort,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // sort 파라미터 체크
        if (!sort.equals("popularity") && !sort.equals("latest")) {
            throw new ParamException("sort 파라미터 입력 값이 올바르지 않습니다.");
        }

        SortOption sortOption = sort.equalsIgnoreCase("popularity") ? SortOption.POPULARITY : SortOption.LATEST;
        List<ReviewsResponse> response = reviewQueryService.fetchEvaluationsWithComments((long) restaurantId, user.id(), sortOption);

        return new ResponseEntity<>(response.stream().map(RestaurantCommentDTO::fromV2).toList(), HttpStatus.OK);
    }

    // 평가 댓글 달기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    public ResponseEntity<RestaurantCommentDTO> postReply(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @RequestBody String commentBody,
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        int evaluationId = commentId - EVALUATION_ID_OFFSET;
        // 댓글 내용 없는 경우 예외 처리
        if (commentBody.trim().isEmpty()) {
            throw new ParamException("대댓글 내용이 없습니다.");
        }
        // 댓글 내용 전처리
        commentBody = commentBody.replaceAll("^\"|\"$", ""); // 양쪽 큰 따옴표 제거
        commentBody = commentBody.replace("\\\"", "\"");     // 이스케이프된 따옴표 복구

        // 평가 댓글 달기
        EvalComment evalComment = evalCommCommandService.create((long) evaluationId, (long) restaurantId, user.id(), new EvalCommentRequest(commentBody));
        User userById = userService.getUserById(user.id());

        return new ResponseEntity<>(RestaurantCommentDTO.fromV2(evalComment, userById), HttpStatus.OK);
    }

    // 식당 댓글 및 대댓글 삭제하기
    @DeleteMapping("/auth/restaurants/{restaurantId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 대댓글인지 평가 코멘트 댓글인지 판단
        if (isSubComment(commentId)) { // 평가에 대한 댓글인 경우
            evalCommCommandService.delete((long) commentId, (long) restaurantId, user.id());
        } else { // 평가인 경우
            throw new ApiStatusException(ErrorCode.GONE);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 댓글 신고하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 현재 신고하기 기능이 없음.
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 평가/댓글 추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/like")
    public ResponseEntity<RestaurantCommentDTO> likeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 평가인 경우
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            int evalId = commentId - EVALUATION_ID_OFFSET;
            EvalReactionResponse response = evaluationReactionService.setEvaluationReaction(user.id(), (long) evalId, ReactionType.LIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        } else { // 평가에 대한 댓글인 경우
            EvalCommentReactionResponse response = evalCommUserReactionService.setEvalCommentReaction(user.id(), (long) commentId, ReactionType.LIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        }
    }

    // 평가/댓글 추천하기
    @PostMapping("/auth/restaurants/{restaurantId}/comments/{commentId}/dislike")
    public ResponseEntity<RestaurantCommentDTO> dislikeComment(
            @PathVariable Integer restaurantId,
            @PathVariable Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        // 평가인 경우
        if (!isSubComment(commentId)) { // 평가 댓글인 경우
            int evalId = commentId - EVALUATION_ID_OFFSET;
            EvalReactionResponse response = evaluationReactionService.setEvaluationReaction(user.id(), (long) evalId, ReactionType.DISLIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        } else { // 평가에 대한 댓글인 경우
            EvalCommentReactionResponse response = evalCommUserReactionService.setEvalCommentReaction(user.id(), (long) commentId, ReactionType.DISLIKE);

            return ResponseEntity.ok(RestaurantCommentDTO.fromV2(response));
        }
    }

    private boolean isSubComment(Integer id) {
        return id <= EVALUATION_ID_OFFSET;
    }
}
