package com.kustaurant.kustaurant.post.comment.controller.api;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommentDeleteResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommentResponse;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostCommentApiController {
    private final PostCommentService postCommentService;
    private final UserService userService;

    // 1. 댓글 or 대댓글 생성
    @PostMapping("/v2/auth/posts/{postId}/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성 (생성한 댓글 반환)",
            description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 생성한 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 혹은 대댓글 생성이 완료", content = {@Content(schema = @Schema(implementation = PostCommentResponse.class))}),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostCommentResponse> postCommentCreateReturnCommentList(
            @PathVariable Integer postId,
            @Valid @RequestBody PostCommentRequest req,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostComment created = postCommentService.create(postId, req, user.id());
        User writer = userService.getUserById(user.id());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(PostCommentResponse.from(created,writer));
    }

    // 2. 댓글, 대댓글 삭제
    @DeleteMapping("/v2/auth/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글 ID를 입력받고 해당 댓글 및 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = PostCommentDeleteResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 id의 댓글이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostCommentDeleteResponse> commentDelete(
            @PathVariable @Parameter(description = "삭제할 댓글 ID", example = "123") Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok(postCommentService.delete(commentId, user.id()));
    }

}
