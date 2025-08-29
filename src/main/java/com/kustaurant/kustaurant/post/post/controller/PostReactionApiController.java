package com.kustaurant.kustaurant.post.post.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.post.controller.response.ScrapToggleResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.service.PostReactionService;
import com.kustaurant.kustaurant.post.post.service.PostScrapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostReactionApiController {
    private final PostScrapService postScrapService;
    private final PostReactionService postReactionService;

    // 1. 게시글 좋아요/싫어요
    @PostMapping("/api/v2/auth/community/{postId}/likes")
    @Operation(summary = "게시글 좋아요/싫어요 토글",
            description = "게시글 ID를 입력받아 좋아요/싫어요 토글. " +
                    "\n\nstatus(LIKE,DISLIKE,null)와 현재 게시글의 좋아요/싫어요 수를 반환합니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 처리가 완료되었습니다", content = @Content(schema = @Schema(implementation = PostReactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostReactionResponse> postLikeCreate(
            @PathVariable Long postId,
            @RequestParam ReactionType reaction,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse response = postReactionService.toggleLike(postId, user.id(),reaction);
        return ResponseEntity.ok(response);
    }

    // 2. 게시글 스크랩
    @PostMapping("/api/v2/auth/community/{postId}/scraps")
    @Operation(summary = "게시글 스크랩",
            description = "게시글 ID를 입력받아 스크랩을 생성하거나 해제합니다. " +
                    "\n\nstatus와 현재 게시글의 스크랩 수를 반환합니다. " +
                    "\n\n처음 스크랩을 누르면 스크랩이 처리되고 status 값으로 SCRAPPED가 반환되며, " +
                    "이미 스크랩된 상태였으면 해제되면서 status 값으로 NOT_SCRAPPED가 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 스크랩 처리가 완료되었습니다", content = @Content(schema = @Schema(implementation = ScrapToggleResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ScrapToggleResponse> createScrap(
            @PathVariable Long postId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        ScrapToggleResponse scrapToggleDTO = postScrapService.toggleScrapWithCount(postId, user.id());
        return ResponseEntity.ok(scrapToggleDTO);
    }
}
