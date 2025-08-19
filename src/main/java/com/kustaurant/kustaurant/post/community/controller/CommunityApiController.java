package com.kustaurant.kustaurant.post.community.controller;


import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.community.controller.request.PostListRequest;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.post.community.service.PostQueryService;
import com.kustaurant.kustaurant.post.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommunityApiController {
    private final PostQueryService postQueryService;
    private final PostService postService;

    // 1. 커뮤니티 메인 화면
    @GetMapping("/api/v2/community/posts")
    @Operation(
            summary = "커뮤니티 메인화면의 게시글 리스트 불러오기",
            description = "게시판 종류와 페이지, 정렬 방법을 입력받고 해당 조건에 맞는 게시글 리스트가 반환됩니다, 현재 인기순으로 설정했을 때는 좋아요가 3이상인 게시글만 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트를 반환하는데 성공하였습니다.", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = PostListResponse.class)))}),
            @ApiResponse(responseCode = "404", description = "요청한 조건의 게시글을 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "파라미터 값이 유효하지 않습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    public ResponseEntity<List<PostListResponse>> community(
            @Valid @ParameterObject PostListRequest req
    ) {
        Page<PostListResponse> paging = postQueryService.getPostList(req.page(), req.category(), req.sort());

        return ResponseEntity.ok(paging.getContent());
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/api/v2/community/{postId}")
    @Operation(summary = "게시글 상세 화면", description = "게시글 ID를 받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(schema = @Schema(implementation = PostDetailResponse.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<PostDetailResponse> post(
            @PathVariable @Parameter(description = "게시글 id", example = "69") Integer postId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        postService.increaseVisitCount(postId);
        return ResponseEntity.ok(postQueryService.getPostDetail(postId, user.id()));
    }

}
