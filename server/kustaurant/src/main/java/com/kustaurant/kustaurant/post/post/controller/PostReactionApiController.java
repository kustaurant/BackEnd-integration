package com.kustaurant.kustaurant.post.post.controller;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostScrapResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostReactionApiController {
    private final PostScrapService postScrapService;
    private final PostReactionService postReactionService;

    // 1. 게시글 좋아요/싫어요
    @PutMapping("/v2/auth/community/{postId}/reaction")
    @Operation(summary = "게시글 좋아요/싫어요 토글",
            description = "게시글 ID를 입력받아 좋아요/싫어요 토글. " +
                    "\n\n유저의 반응상태(LIKE,DISLIKE,null)와 현재 게시글의 좋아요/싫어요 수를 반환합니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 처리가 완료되었습니다", content = @Content(schema = @Schema(implementation = PostReactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostReactionResponse> setPostReaction(
            @PathVariable Long postId,
            @Parameter(description = "목표 반응 상태(좋아요=LIKE,싫어요=DISLIKE,취소=값 전달x). 미전달 시 해제")
            @RequestParam(required = false) ReactionType cmd,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostReactionResponse response = postReactionService.setPostReaction(postId, user.id(), cmd);
        return ResponseEntity.ok(response);
    }

    // 2. 게시글 스크랩
    @PostMapping("/v2/auth/community/{postId}/scraps")
    @Operation(summary = "게시글 스크랩",
            description = "게시글 ID를 입력받아 스크랩을 생성하거나 해제합니다. " +
                    "\n\nstatus와 현재 게시글의 스크랩 수를 반환합니다. " +
                    "\n\n처음 스크랩을 누르면 스크랩이 처리되고 status 값으로 SCRAPPED가 반환되며, " +
                    "이미 스크랩된 상태였으면 해제되면서 status 값으로 NOT_SCRAPPED가 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 스크랩 처리가 완료되었습니다", content = @Content(schema = @Schema(implementation = PostScrapResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostScrapResponse> setPostScrap(
            @PathVariable Long postId,
            @Parameter(description = "true=스크랩, false=해제", required = true, example = "true")
            @RequestParam boolean scrapped,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        PostScrapResponse scrapToggleDTO = postScrapService.toggleScrapWithCount(postId, user.id(), scrapped);
        return ResponseEntity.ok(scrapToggleDTO);
    }
}
