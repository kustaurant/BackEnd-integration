package com.kustaurant.kustaurant.post.comment.controller.web;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.exception.auth.UnauthenticatedException;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService postCommentService;

    // 1. 댓글 or 대댓글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable Integer postId,
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "parentCommentId", required = false) Integer parentCommentId,
            @AuthUser AuthUserInfo user
    ) {
        postCommentService.createComment(content, postId, parentCommentId, user.id());
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    // 2. 댓글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        // 댓글 조회 및 작성자 권한 확인
        PostComment comment = postCommentService.getPostCommentByCommentId(commentId);
        if (!comment.getUserId().equals(user.id())) {
            throw new UnauthenticatedException("댓글을 삭제할 권한이 없습니다.");
        }

        int deletedCount = postCommentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "deletedCount", deletedCount,
                "message", "comment delete complete"
        ));
    }

    // 3. 댓글 입력창 포커스시 로그인 상태 확인
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/login/comment-write")
    public ResponseEntity<String> checkLogin(
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
    }
}
