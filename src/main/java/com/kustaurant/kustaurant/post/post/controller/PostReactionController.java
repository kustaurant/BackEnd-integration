package com.kustaurant.kustaurant.post.post.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.post.controller.response.PostScrapResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.service.PostReactionService;
import com.kustaurant.kustaurant.post.post.service.PostScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class PostReactionController {
    private final PostReactionService reactionService;
    private final PostScrapService scrapService;

    // 게시글 좋아요/싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/posts/{postId}/reaction")
    public ResponseEntity<PostReactionResponse> setPostReaction(
            @PathVariable Long postId,
            @RequestParam(required = false) ReactionType reaction,
            @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse reactionToggleResponse = reactionService.setPostReaction(postId, user.id(), reaction);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 스크랩
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/posts/{postId}/scrap")
    public ResponseEntity<PostScrapResponse> setPostScrap(
            @PathVariable Long postId,
            @RequestParam boolean scrapped,
            @AuthUser AuthUserInfo user
    ) {
        PostScrapResponse response = scrapService.toggleScrapWithCount(postId, user.id(), scrapped);
        return ResponseEntity.ok(response);
    }
}
