package com.kustaurant.kustaurant.post.comment.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.service.PostCommentReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostCommReactionApiController {
    private final PostCommentReactionService reactionService;

    // 1. 댓글 좋아요
    @PostMapping("/v2/auth/community/comments/{commentId}/like")
    @Operation(summary = "커뮤니티 포스트 댓글 좋아요",
            description = "입력된 댓글 ID에 대한 좋아요 토글 ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 완료", content = @Content(schema = @Schema(implementation = PostCommReactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 댓글을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostCommReactionResponse> togglePostCommentLike(
            @PathVariable @Parameter(description = "댓글 id", example = "30") Long commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostCommReactionResponse response = reactionService.toggleUserReaction(commentId,user.id(), ReactionType.LIKE);
        return ResponseEntity.ok(response);
    }

    // 2. 댓글 싫어요
    @PostMapping("/v2/auth/community/comments/{commentId}/dislike")
    @Operation(summary = "커뮤니티 포스트 댓글 싫어요",
            description = "입력된 댓글 ID에 대한 싫어요 토글")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리 완료", content = @Content(schema = @Schema(implementation = PostCommReactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 댓글을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostCommReactionResponse> togglePostCommentDislike(
            @PathVariable @Parameter(description = "댓글 id입니다", example = "30") Long commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostCommReactionResponse response = reactionService.toggleUserReaction(commentId,user.id(), ReactionType.DISLIKE);
        return ResponseEntity.ok(response);
    }
}
