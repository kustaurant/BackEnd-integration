package com.kustaurant.kustaurant.post.community.controller;

import com.kustaurant.kustaurant.common.view.ViewCountService;
import com.kustaurant.kustaurant.common.view.ViewResourceType;
import com.kustaurant.kustaurant.common.view.ViewerKeyProvider;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.community.controller.request.PostListRequest;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.post.controller.response.PostResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.community.service.PostQueryService;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {
    private final PostQueryService postQueryService;

    private final ViewerKeyProvider viewerKeyProvider;
    private final ViewCountService viewCountService;

    // 1. 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(
            @Valid @ModelAttribute PostListRequest req,
            Model model
    ) {
        Page<PostListResponse> paging = postQueryService.getPostPagingList(req.page(), req.category(), req.sort());

        model.addAttribute("currentPage", "community");
        model.addAttribute("postCategory", req.category());
        model.addAttribute("sort", req.sort());
        model.addAttribute("paging", paging);
        model.addAttribute("postList", paging.getContent());
        return "community/community";
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/community/{postId}")
    public String postDetail(
            Model model,
            @PathVariable Long postId,
            @AuthUser AuthUserInfo user,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        String viewerKey = viewerKeyProvider.resolveViewerKey(user, req, res);
        viewCountService.countOncePerHour(ViewResourceType.POST, postId, viewerKey);

        PostDetailResponse response = postQueryService.getPostDetail(postId, user.id());
        model.addAttribute("view", response);
        return "community/community_post";
    }

    // 3. 게시글 검색 화면
    @GetMapping("/community/search")
    public String search(
            Model model,
            @Valid @ModelAttribute PostListRequest req,
            @RequestParam(value = "kw", defaultValue = "") String kw
    ) {
        Page<PostListResponse> paging = postQueryService.searchLatest(req.page(), req.category(), kw);

        model.addAttribute("paging", paging);
        model.addAttribute("postCategory", req.category());
        model.addAttribute("postSearchKw", kw);
        model.addAttribute("sort", req.sort());
        model.addAttribute("postList", paging.getContent());
        return "community/community";
    }

    // 4. 커뮤니티 게시글 작성 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/community/write")
    public String writePost() {
        return "community/community_write";
    }

    // 6. 게시글 수정 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/community/post/update")
    public String updatePost(
            @RequestParam Long postId,
            Model model,
            @AuthUser AuthUserInfo user
    ) {
        PostResponse response = postQueryService.getPostForUpdate(postId, user.id());
        model.addAttribute("post", response);
        return "community/community_update";
    }
}
