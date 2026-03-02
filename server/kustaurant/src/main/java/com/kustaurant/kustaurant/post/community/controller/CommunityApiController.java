package com.kustaurant.kustaurant.post.community.controller;


import com.kustaurant.kustaurant.common.view.ViewCountService;
import com.kustaurant.kustaurant.common.view.ViewResourceType;
import com.kustaurant.kustaurant.common.view.ViewerKeyProvider;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.post.community.controller.request.PostListRequest;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.community.service.PostQueryService;
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
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class CommunityApiController implements CommunityApiDoc {
    private final PostQueryService postQueryService;

    private final ViewerKeyProvider viewerKeyProvider;
    private final ViewCountService viewCountService;

    // 1. 커뮤니티 메인 화면
    @GetMapping("/v2/community/posts")
    public ResponseEntity<List<PostListResponse>> community(
            @Valid @ParameterObject PostListRequest req
    ) {
        Page<PostListResponse> paging = postQueryService.getPostPagingList(req.page(), req.category(), req.sort());

        return ResponseEntity.ok(paging.getContent());
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/v2/community/{postId}")
    public ResponseEntity<PostDetailResponse> post(
            @PathVariable Long postId,
            @AuthUser AuthUserInfo user,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        String viewerKey = viewerKeyProvider.resolveViewerKey(user, req, res);
        viewCountService.countOncePerHour(ViewResourceType.POST, postId, viewerKey);

        return ResponseEntity.ok(postQueryService.getPostDetail(postId, user.id()));
    }
}
