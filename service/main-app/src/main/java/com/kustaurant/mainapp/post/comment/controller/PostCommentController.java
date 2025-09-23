package com.kustaurant.mainapp.post.comment.controller;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.mainapp.post.comment.controller.response.PostCommentDeleteResponse;
import com.kustaurant.mainapp.post.comment.controller.response.PostCommentResponse;
import com.kustaurant.mainapp.post.comment.domain.PostComment;
import com.kustaurant.mainapp.post.comment.service.PostCommentService;
import com.kustaurant.mainapp.user.user.controller.port.UserService;
import com.kustaurant.mainapp.user.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
@RequiredArgsConstructor
public class PostCommentController {
    private final PostCommentService postCommentService;
    private final UserService userService;

    // 1. 댓글 or 대댓글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<PostCommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody PostCommentRequest req,
            @AuthUser AuthUserInfo user
    ) {
        PostComment created = postCommentService.create(postId, req, user.id());
        User writer = userService.getUserById(user.id());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();

        return ResponseEntity.created(location).body(PostCommentResponse.from(created,writer));
    }

    // 2. 댓글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<PostCommentDeleteResponse> deleteComment(
            @PathVariable Long commentId,
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok(postCommentService.delete(commentId, user.id()));
    }

    // 3. 댓글 입력창 포커스시 로그인 상태 확인
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/login/comment-write")
    public ResponseEntity<String> checkLogin() {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
    }
}
