package com.kustaurant.kustaurant.post.comment.controller.api;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.controller.response.CommentReactionResponse;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommentDTO;
import com.kustaurant.kustaurant.post.comment.service.PostCommentApiService;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.service.PostQueryApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostCommentApiController {
    private final PostCommentApiService postCommentApiService;
    private final PostQueryApiService postQueryApiService;


    // 1_1. 댓글 or 대댓글 생성
    @PostMapping("/api/v1/auth/community/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성 (생성한 댓글 반환)", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 생성한 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 혹은 대댓글 생성이 완료되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostCommentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostCommentDTO> postCommentCreateReturnCommentList(
            @RequestParam(name = "content", defaultValue = "")
            @Parameter(description = "생성할 댓글의 내용입니다. 최소 1자에서 최대 10,000자까지 입력 가능합니다.", example = "이 게시글에 대해 궁금한 점이 있습니다.")
            String content,

            @RequestParam(name = "postId")
            @Parameter(description = "댓글을 추가할 게시글의 ID입니다.", example = "123")
            String postId,

            @RequestParam(name = "parentCommentId", defaultValue = "")
            @Parameter(description = "대댓글을 작성할 경우, 부모 댓글의 ID를 입력합니다. 이 값이 비어 있으면 일반 댓글로 처리됩니다.", example = "456")
            String parentCommentId,

            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostComment postComment = postCommentApiService.createComment(content, postId, user.id());
        // 대댓글 일 경우 부모 댓글과 관계 매핑
        if (!parentCommentId.isEmpty()) {
            postCommentApiService.processParentComment(postComment, parentCommentId);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postCommentApiService.createPostCommentDTOWithFlags(postComment, user.id()));
    }

    // 1_2. 댓글 or 대댓글 생성
    @PostMapping("/api/v2/auth/community/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성 (전체 댓글 목록 반환)", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 전체 댓글 목록을 반환합니다")
    public ResponseEntity<List<PostCommentDTO>> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "")
            @Parameter(description = "생성할 댓글의 내용입니다. 최소 1자에서 최대 10,000자까지 입력 가능합니다.", example = "이 게시글에 대해 궁금한 점이 있습니다.")
            String content,

            @RequestParam(name = "postId")
            @Parameter(description = "댓글을 추가할 게시글의 ID입니다.", example = "123")
            String postId,

            @RequestParam(name = "parentCommentId", defaultValue = "")
            @Parameter(description = "대댓글을 작성할 경우, 부모 댓글의 ID를 입력합니다. 이 값이 비어 있으면 일반 댓글로 처리됩니다.", example = "456")
            String parentCommentId,

            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        Post post = postQueryApiService.getPost(Integer.valueOf(postId));
        PostComment postComment = postCommentApiService.createComment(content, postId, user.id());
        // 대댓글 일 경우 부모 댓글과 관계 매핑
        if (!parentCommentId.isEmpty()) {
            postCommentApiService.processParentComment(postComment, parentCommentId);
        }
        List<PostCommentDTO> postCommentDTOs = postCommentApiService.getPostCommentDTOs(post, user.id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postCommentDTOs);
    }

    // 2. 댓글, 대댓글 삭제
    @DeleteMapping("/api/v1/auth/community/comment/{commentId}")
    @Transactional
    @Operation(summary = "댓글 삭제", description = "댓글 ID를 입력받고 해당 댓글 및 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제에 성공하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 id의 댓글이 존재하지 않습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> commentDelete(
            @PathVariable @Parameter(description = "삭제할 댓글의 ID입니다.", example = "123") Integer commentId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        postCommentApiService.deleteComment(commentId, user.id());
        return ResponseEntity.noContent().build();
    }

    // 3. 댓글 좋아요/싫어요 처리
    @PostMapping("/api/v1/auth/community/comments/{commentId}/{action}")
    @Operation(summary = "댓글 좋아요/싫어요", description = "댓글 ID를 입력받아 댓글에 대한 좋아요 또는 싫어요를 토글합니다. \n\n 댓글에 대한 유저의 현재 상태를 나타내는 commentLikeStatus와 좋아요, 싫어요 개수를 반환합니다. \n\n commentLikeStatus는 1 (좋아요), -1 (싫어요), 0 (아무것도 아님) 값으로 분류됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentReactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 댓글을 찾을 수 없습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommentReactionResponse> toggleCommentLikeOrDislike(
            @PathVariable @Parameter(description = "댓글 id입니다", example = "30") Integer commentId,
            @PathVariable @Parameter(description = "좋아요는 likes, 싫어요는 dislikes로 값을 설정합니다.", example = "likes") String action,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostComment postComment = postCommentApiService.getPostCommentByCommentId(commentId);
        int commentLikeStatus = postCommentApiService.toggleCommentLikeOrDislike(action,user.id(),commentId).getStatus().toAppLikeStatus();
        CommentReactionResponse responseDTO = CommentReactionResponse.toCommentLikeDislikeDTO(postComment,commentLikeStatus);
        return ResponseEntity.ok(responseDTO);
    }
}
