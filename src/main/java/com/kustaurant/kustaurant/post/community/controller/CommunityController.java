package com.kustaurant.kustaurant.post.community.controller;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.community.controller.request.PostListRequest;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.service.PostQueryService;
import groovy.util.logging.Slf4j;
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

    // 1. 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(
            @Valid @ModelAttribute PostListRequest req,
            Model model
    ) {
        Page<PostListResponse> paging = postQueryService.getPostList(req.page(), 10, req.category(), req.sort());

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
            @PathVariable Integer postId,
            @AuthUser AuthUserInfo user
    ) {
        PostDetailResponse res = postQueryService.getPostDetail(postId, user.id());
        model.addAttribute("view", res);
        return "community/community_post";
    }

    // 3. 게시글 검색 화면
    @GetMapping("/community/search")
    public String search(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(defaultValue = "LATEST") SortOption sort,
            @RequestParam(defaultValue = "ALL") PostCategory postCategory
    ) {
        Page<PostDetailResponse> paging = postQueryService.getList(page, sort, kw, postCategory);
        model.addAttribute("paging", paging);
        model.addAttribute("postSearchKw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("postCategory", postCategory);
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
            @RequestParam Integer postId,
            Model model,
            @AuthUser AuthUserInfo user
    ) {
        Post post = postQueryService.getPost(postId);
        model.addAttribute("post", PostDetailResponse.from(post));
        return "community/community_update";
    }

}
