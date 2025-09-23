package com.kustaurant.mainapp.post.comment.controller;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.mainapp.post.comment.service.PostCommentReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PostCommReactionController {
    private final PostCommentReactionService reactionService;

    //1. 게시글 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/comments/{commentId}/reaction")
    public ResponseEntity<PostCommReactionResponse> setPostCommentReaction(
            @PathVariable Long commentId,
            @RequestParam(required = false) ReactionType reaction,
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok(reactionService.setPostCommentReaction(commentId, user.id(), reaction));
    }

}
