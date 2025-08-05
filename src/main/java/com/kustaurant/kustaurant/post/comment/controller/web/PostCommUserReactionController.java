package com.kustaurant.kustaurant.post.comment.controller.web;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PostCommUserReactionController {
    private final PostCommentService postCommentService;

    //1. 게시글 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/like")
    public ResponseEntity<PostReactionResponse> toggleCommentLike(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse reactionToggleResponse = postCommentService.toggleLike(user.id(), commentId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    //2. 게시글 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/dislike")
    public ResponseEntity<PostReactionResponse> toggleCommentDislike(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse reactionToggleResponse = postCommentService.toggleDislike(user.id(), commentId);
        return ResponseEntity.ok(reactionToggleResponse);
    }
}
