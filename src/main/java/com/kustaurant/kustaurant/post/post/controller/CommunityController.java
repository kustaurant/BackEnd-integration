package com.kustaurant.kustaurant.post.post.controller;

import com.kustaurant.kustaurant.global.exception.exception.auth.UnauthenticatedException;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.domain.PostDetailView;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.post.post.service.web.PostScrapService;
import com.kustaurant.kustaurant.post.post.service.web.PostQueryService;
import com.kustaurant.kustaurant.post.post.service.web.PostCommandService;
import com.kustaurant.kustaurant.post.post.service.StorageService;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import java.util.*;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {
    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final PostCommentService postCommentService;
    private final PostScrapService postScrapService;
    private final StorageService storageService;

    // 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(
            Model model,
            @RequestParam(defaultValue = "전체") String postCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        Page<PostDTO> paging;
        if (postCategory.equals("전체")) {
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

    // 커뮤니티 게시글 상세 화면
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

    // 게시글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId, @AuthUser AuthUserInfo user) {
        log.info("Deleting post with ID: {} by user: {}", postId, user.id());

        // 게시글 조회 및 작성자 권한 확인
        Post post = postQueryService.getPost(postId);
        if (!post.getAuthorId().equals(user.id())) {
            throw new UnauthenticatedException("게시글을 삭제할 권한이 없습니다.");
        }

        postCommandService.deletePost(postId);
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }

    // 댓글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Integer commentId, @AuthUser AuthUserInfo user) {
        // 댓글 조회 및 작성자 권한 확인
        com.kustaurant.kustaurant.post.comment.domain.PostComment comment = postCommentService.getPostCommentByCommentId(commentId);
        if (!comment.getUserId().equals(user.id())) {
            throw new UnauthenticatedException("댓글을 삭제할 권한이 없습니다.");
        }

        int deletedCount = postCommentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "deletedCount", deletedCount,
                "message", "comment delete complete"
        ));
    }

    // 댓글 or 대댓글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable Integer postId,
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "parentCommentId", required = false) Integer parentCommentId,
            @AuthUser AuthUserInfo user
    ) {
        postCommentService.createComment(content, postId, parentCommentId, user.id());
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    // 게시글 좋아요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/like")
    public ResponseEntity<ReactionToggleResponse> togglePostLike(@PathVariable Integer postId, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postCommandService.toggleLike(postId, userId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 싫어요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/dislike")
    public ResponseEntity<ReactionToggleResponse> togglePostDislike(@PathVariable Integer postId, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postCommandService.toggleDislike(postId, userId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 스크랩
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts/{postId}/scrap")
    public ResponseEntity<Map<String, Object>> toggleScrap(@PathVariable Integer postId, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        Map<String, Object> response = postScrapService.toggleScrap(userId, postId);
        return ResponseEntity.ok(response);
    }

    // 게시글 검색 화면
    @GetMapping("/community/search")
    public String search(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(defaultValue = "recent") String sort, @RequestParam(defaultValue = "전체") String postCategory) {

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

    // 게시글 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/like")
    public ResponseEntity<ReactionToggleResponse> toggleCommentLike(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        ReactionToggleResponse reactionToggleResponse = postCommentService.toggleLike(user.id(), commentId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comments/{commentId}/dislike")
    public ResponseEntity<ReactionToggleResponse> toggleCommentDislike(
            @PathVariable Integer commentId,
            @AuthUser AuthUserInfo user
    ) {
        ReactionToggleResponse reactionToggleResponse = postCommentService.toggleDislike(user.id(), commentId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 커뮤니티 게시글 작성 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/community/write")
    public String writePost(
            @AuthUser AuthUserInfo user
    ) {
        return "community/community_write";
    }

    // 게시글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts")
    public ResponseEntity<String> createPost(
            @RequestParam("title") String title,
            @RequestParam("postCategory") String category,
            @RequestParam("content") String content,
            @AuthUser AuthUserInfo user
    ) {
        postCommandService.create(title, category, content, user.id());
        return ResponseEntity.ok("글이 성공적으로 저장되었습니다.");
    }

    //게시글 수정 화면
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

    // 게시글 수정
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Integer postId,
            @RequestParam String title,
            @RequestParam String postCategory,
            @RequestParam String content,
            @AuthUser AuthUserInfo user
    ) {
        Post post = postQueryService.getPost(postId);
        if (!post.getAuthorId().equals(user.id())) {
            throw new UnauthenticatedException("게시글을 수정할 권한이 없습니다.");
        }
        postCommandService.update(postId, title, postCategory, content);
        return ResponseEntity.ok("글이 성공적으로 수정되었습니다.");
    }

    // 이미지 업로드 (미리보기)
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

    // 댓글 입력창 포커스시 로그인 상태 확인
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/login/comment-write")
    public ResponseEntity<String> checkLogin(
            @AuthUser AuthUserInfo user
    ) {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
    }
}
