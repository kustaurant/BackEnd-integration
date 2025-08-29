package com.kustaurant.kustaurant.post.post.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.post.controller.response.ScrapToggleResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.service.PostReactionService;
import com.kustaurant.kustaurant.post.post.service.PostScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class PostReactionController {
    private final PostReactionService reactionService;
    private final PostScrapService scrapService;

    // 게시글 좋아요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/like")
    public ResponseEntity<PostReactionResponse> togglePostLike(
            @PathVariable Long postId,
            @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse reactionToggleResponse = reactionService.toggleLike(postId, user.id(), ReactionType.LIKE);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 싫어요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/dislike")
    public ResponseEntity<PostReactionResponse> togglePostDislike(
            @PathVariable Long postId, @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse reactionToggleResponse = reactionService.toggleLike(postId, user.id(), ReactionType.DISLIKE);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 스크랩
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/scrap")
    public ResponseEntity<ScrapToggleResponse> toggleScrap(
            @PathVariable Long postId, @AuthUser AuthUserInfo user
    ) {
        ScrapToggleResponse response = scrapService.toggleScrapWithCount(postId, user.id());
        return ResponseEntity.ok(response);
    }
}
