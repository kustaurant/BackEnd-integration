package com.kustaurant.kustaurant.post.post.controller.web;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.controller.response.PostDTO;
import com.kustaurant.kustaurant.post.post.controller.response.PostDetailView;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.service.PostQueryService;
import com.kustaurant.kustaurant.post.post.service.S3Service;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {
    private final PostQueryService postQueryService;
    private final PostCommentService postCommentService;
    private final S3Service storageService;

    // 1. 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(
            Model model,
            @RequestParam(defaultValue = "ALL") PostCategory postCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "LATEST") SortOption sort
    ) {
        Page<PostDTO> paging;
        if (postCategory == PostCategory.ALL) {
            paging = postQueryService.getList(page, sort);
        } else {
            paging = postQueryService.getListByPostCategory(postCategory, page, sort);
        }

        model.addAttribute("currentPage", "community");
        model.addAttribute("postCategory", postCategory);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);
        model.addAttribute("postList", paging.getContent());
        return "community/community";
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/community/{postId}")
    public String postDetail(
            Model model,
            @PathVariable Integer postId,
            @AuthUser AuthUserInfo user,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        PostDetailView view = postCommentService.buildPostDetailView(postId, user.id(), sort);
        model.addAttribute("view", view);
        return "community/community_post";
    }

    // 3. 게시글 검색 화면
    @GetMapping("/community/search")
    public String search(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(defaultValue = "LATEST") SortOption sort, @RequestParam(defaultValue = "ALL") PostCategory postCategory) {

        Page<PostDTO> paging = postQueryService.getList(page, sort, kw, postCategory);
        List<String> timeAgoList = postQueryService.getTimeAgoList(paging);
        model.addAttribute("timeAgoList", timeAgoList);
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
    public String writePost(
            @AuthUser AuthUserInfo user
    ) {
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
        model.addAttribute("post", PostDTO.from(post));
        return "community/community_update";
    }



    // 8. 이미지 업로드 (미리보기)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/images")
    public ResponseEntity<?> imageUpload(
            @RequestParam("image") MultipartFile imageFile,
            @AuthUser AuthUserInfo user
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("rs_st", -1, "rs_msg", "파일이 없습니다."));
        }

        try {
            // StorageService를 통해 이미지를 S3에 저장하고, URL을 받아옵니다.
            String imageUrl = storageService.storeImage(imageFile);
            String thumbnailUrl = imageUrl; // 실제로는 썸네일 URL을 생성하거나 처리해야 할 수 있습니다.

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("thumbnailPath", thumbnailUrl);
            fileInfo.put("fullPath", imageUrl);
            fileInfo.put("orgFilename", imageFile.getOriginalFilename());

            Map<String, Object> response = new HashMap<>();
            response.put("rs_st", 0); // 성공 상태 코드
            response.put("rs_data", fileInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("rs_st", -1, "rs_msg", "이미지 업로드 실패"));
        }
    }
}
