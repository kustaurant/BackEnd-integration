package com.kustaurant.kustaurant.evaluation.comment.controller.api;

import com.kustaurant.kustaurant.evaluation.comment.controller.port.EvalCommCommandService;
import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EvalCommentApiController {
    private final EvalCommCommandService evalCommCommandService;
    private final UserService userService;


    // 1. 평가에 댓글 달기
    @Operation(summary = "평가에 댓글 달기", description = "작성한 평가 댓글을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EvalCommentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "댓글은10자 이상 1000자 이하여야함. 해당 식당에 해당 comment Id를 가진 comment가 없음", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없음.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @PostMapping("/api/v1/auth/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<EvalCommentResponse> addEvaluationComment(
            @PathVariable Integer restaurantId,
            @PathVariable Long evalCommentId,
            @Valid @RequestBody EvalCommentRequest req,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        EvalComment evalComment = evalCommCommandService.create(evalCommentId, restaurantId, user.id(), req);
        User cuurrentUser = userService.getUserById(user.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EvalCommentResponse.from(evalComment,cuurrentUser,null,user.id()));
    }


    // 2. 평가 댓글 삭제하기
    @Operation(summary = "리뷰 댓글 및 대댓글 삭제하기", description = "리뷰 댓글 및 대댓글 삭제하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제에 성공.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))}),
            @ApiResponse(responseCode = "400", description = "restaurantId 식당에 해당 comment Id를 가진 comment가 없는 경우 400을 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "restaurantId에 해당하는 식당이 없는 경우 404를 반환합니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @DeleteMapping("/api/v1/auth/restaurants/{restaurantId}/comments/{evalCommentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer restaurantId,
            @PathVariable Long evalCommentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        evalCommCommandService.delete(evalCommentId, restaurantId, user.id());

        return ResponseEntity.noContent().build();
    }

}
