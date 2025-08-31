package com.kustaurant.kustaurant.post.comment.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.service.PostCommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PostCommReactionController {
    private final PostCommentReactionService reactionService;

    //1. 게시글 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/like")
    public ResponseEntity<PostCommReactionResponse> toggleCommentLike(
            @PathVariable Long commentId,
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok(reactionService.toggleUserReaction(commentId, user.id(), ReactionType.LIKE));
    }

    //2. 게시글 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/dislike")
    public ResponseEntity<PostCommReactionResponse> toggleCommentDislike(
            @PathVariable Long commentId,
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok(reactionService.toggleUserReaction(commentId, user.id(), ReactionType.DISLIKE));
    }
}
