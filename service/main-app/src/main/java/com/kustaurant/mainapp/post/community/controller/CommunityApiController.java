package com.kustaurant.mainapp.post.community.controller;


import com.kustaurant.mainapp.common.view.ViewCountService;
import com.kustaurant.mainapp.common.view.ViewResourceType;
import com.kustaurant.mainapp.common.view.ViewerKeyProvider;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.global.exception.ApiErrorResponse;
import com.kustaurant.mainapp.post.community.controller.request.PostListRequest;
import com.kustaurant.mainapp.post.community.controller.response.PostListResponse;
import com.kustaurant.mainapp.post.community.controller.response.PostDetailResponse;
import com.kustaurant.mainapp.post.community.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommunityApiController {
    private final PostQueryService postQueryService;

    private final ViewerKeyProvider viewerKeyProvider;
    private final ViewCountService viewCountService;

    // 1. 커뮤니티 메인 화면
    @Operation(
            summary = "커뮤니티 메인화면의 게시글 리스트 불러오기",
            description = "게시판 종류와 페이지, 정렬 방법을 입력받고 해당 조건에 맞는 게시글 리스트가 반환됩니다, 현재 인기순으로 설정했을 때는 좋아요가 3이상인 게시글만 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트를 반환하는데 성공하였습니다.", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = PostListResponse.class)))}),
            @ApiResponse(responseCode = "404", description = "요청한 조건의 게시글을 찾을 수 없습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "파라미터 값이 유효하지 않습니다.", content = {@Content(schema = @Schema(implementation = ApiErrorResponse.class))})
    })
    @GetMapping("/v2/community/posts")
    public ResponseEntity<List<PostListResponse>> community(
            @Valid @ParameterObject PostListRequest req
    ) {
        Page<PostListResponse> paging = postQueryService.getPostPagingList(req.page(), req.category(), req.sort());

        return ResponseEntity.ok(paging.getContent());
    }

    // 2. 커뮤니티 게시글 상세 화면
    @Operation(
            summary = "게시글 상세 화면(로그인/비로그인 공용),(조회수 기능 포함)",
            description = """
                    **로그인 상태**: 인증 정보가 있으면 사용자 컨텍스트로 동작합니다.
                    **비로그인 상태**: 비로그인 사용자는 `X-Device-Id` 헤더로 기기 식별자를 보내주세요.
                    헤더가 없으면 서버는 새 익명 ID를 발급하여 `X-anonymous-id` 응답 헤더로 내려줍니다.
                    클라이언트는 저장후 다음부터 `X-device-Id`로 전송해 주세요.
                    조회수는 사용자 별로 1시간 기준 1회만 증가합니다.
                    """,
    parameters = {@Parameter(
            name = "X-device-id",
            in = ParameterIn.HEADER,
            required = false,
            description = "비로그인 앱 식별자. 조회수 증가가 발생하는 호출에서는 반드시 포함",
            examples = {@ExampleObject(name = "예시", value = "a2c2ae2b-4d1a-4b3b-9d27-1b7b2d55a8c9")}
    )})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(schema = @Schema(implementation = PostDetailResponse.class))},
                    headers = {@Header(name = "X-Anonymous-Id", description = "서버가 새 익명 ID를 발급한 경우 내려줍니다. 이후 요청부터 X-Device-Id로 전송하세요.", schema = @Schema(type = "string"))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/v2/community/{postId}")
    public ResponseEntity<PostDetailResponse> post(
            @PathVariable @Parameter(description = "게시글 id", example = "21") Long postId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Parameter(hidden = true) HttpServletRequest req,
            @Parameter(hidden = true) HttpServletResponse res
    ) {
        String viewerKey = viewerKeyProvider.resolveViewerKey(user, req, res);
        viewCountService.countOncePerHour(ViewResourceType.POST, postId, viewerKey);

        return ResponseEntity.ok(postQueryService.getPostDetail(postId, user.id()));
    }

}
