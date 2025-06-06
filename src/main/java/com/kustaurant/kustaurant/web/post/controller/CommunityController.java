package com.kustaurant.kustaurant.web.post.controller;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.common.post.domain.*;
import com.kustaurant.kustaurant.common.user.controller.port.UserService;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.web.comment.PostCommentService;
import com.kustaurant.kustaurant.web.post.service.PostScrapService;
import com.kustaurant.kustaurant.web.post.service.PostService;
import com.kustaurant.kustaurant.common.post.service.StorageService;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.*;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostScrapService postScrapService;
    private final StorageService storageService;
    private final UserService userService;

    // 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(Model model, @RequestParam(defaultValue = "전체") String postCategory, @RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(defaultValue = "recent") String sort) {
        Page<Post> paging;
        if (postCategory.equals("전체")) {
            paging = postService.getList(page, sort);
        } else {
            paging = postService.getListByPostCategory(postCategory, page, sort);
        }

        model.addAttribute("currentPage", "community");
        model.addAttribute("postCategory", postCategory);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);

        List<PostDTO> postDTOList = postService.getDTOs(paging);

        model.addAttribute("postList", postDTOList);
        return "community";
    }

    @GetMapping("/community/{postId}")
    public String postDetail(Model model, @PathVariable Integer postId, Principal principal, @RequestParam(defaultValue = "recent") String sort) {
        Integer userId = (principal != null) ? Integer.valueOf(principal.getName()) : null;
        PostDetailView view = postCommentService.buildPostDetailView(postId, userId, sort);
        model.addAttribute("view", view);
        return "community_post";
    }

    @GetMapping("/api/post/delete")
    public ResponseEntity<String> deletePost(@RequestParam(name = "postId") Integer postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }

    // 댓글 ,대댓글 삭제
    @Transactional
    @GetMapping("/api/comment/delete")
    public ResponseEntity<Map<String, Object>> deleteComment(@RequestParam Integer commentId) {
        int deletedCount = postCommentService.deleteComment(commentId);
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("deletedCount", deletedCount);
        response.put("message", "comment delete complete");

        return ResponseEntity.ok(response);
    }

    // 댓글 or 대댓글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/comment/create")
    public ResponseEntity<String> createComment(@RequestParam(name = "content", defaultValue = "") String content, @RequestParam(name = "postId") Integer postId, @RequestParam(name = "parentCommentId", required = false) Integer parentCommentId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        postCommentService.createComment(content, postId, parentCommentId, userId);
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    // 게시글 좋아요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/like")
    public ResponseEntity<ReactionToggleResponse> togglePostLike(@RequestParam("postId") Integer postId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postService.toggleLike(postId, userId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 싫어요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/dislike")
    public ResponseEntity<ReactionToggleResponse> togglePostDislike(@RequestParam("postId") Integer postId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postService.toggleDislike(postId, userId);
        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 스크랩
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/scrap")
    public ResponseEntity<Map<String, Object>> toggleScrap(@RequestParam("postId") Integer postId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        Map<String, Object> response = postScrapService.toggleScrap(postId, userId);
        return ResponseEntity.ok(response);
    }

    // 게시글 검색 화면
    @GetMapping("/community/search")
    public String search(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(defaultValue = "recent") String sort, @RequestParam(defaultValue = "전체") String postCategory) {

        Page<Post> paging = this.postService.getList(page, sort, kw, postCategory);
        List<String> timeAgoList = postService.getTimeAgoList(paging);
        model.addAttribute("timeAgoList", timeAgoList);
        model.addAttribute("paging", paging);
        model.addAttribute("postSearchKw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("postCategory", postCategory);
        return "community";
    }

    // 게시글 댓글 좋아요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/comment/like/{commentId}")
    public ResponseEntity<ReactionToggleResponse> toggleCommentLike(@PathVariable Integer commentId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postCommentService.toggleLike(userId, commentId);
        log.info("likeCount: {}, dislikeCount: {}, status: {}", reactionToggleResponse.getLikeCount(), reactionToggleResponse.getDislikeCount(), reactionToggleResponse.getStatus());

        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 게시글 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/comment/dislike/{commentId}")
    public ResponseEntity<ReactionToggleResponse> toggleCommentDislike(@PathVariable Integer commentId, Principal principal) {
        Integer userId = Integer.valueOf(principal.getName());
        ReactionToggleResponse reactionToggleResponse = postCommentService.toggleDislike(userId, commentId);
        log.info("likeCount: {}, dislikeCount: {}, status: {}, netlikes:{}", reactionToggleResponse.getLikeCount(), reactionToggleResponse.getDislikeCount(), reactionToggleResponse.getStatus(), reactionToggleResponse.getNetLikes());

        return ResponseEntity.ok(reactionToggleResponse);
    }

    // 커뮤니티 게시글 작성 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/community/write")
    public String writePost() {
        return "community_write";
    }


    // 게시글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/community/post/create")
    public ResponseEntity<String> createPost(@RequestParam("title") String title, @RequestParam("postCategory") String category, @RequestParam("content") String content, Principal principal) throws IOException {
        Integer userId = Integer.valueOf(principal.getName());
        postService.create(title, category, content, userId);
        return ResponseEntity.ok("글이 성공적으로 저장되었습니다.");
    }

    //게시글 수정 화면
    @GetMapping("/community/post/update")
    public String updatePost(@RequestParam Integer postId, Model model) {
        Post post = postService.getPost(postId);
        model.addAttribute("post", post);
        return "community_update";
    }

    // 게시글 수정
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/community/post/update")
    public ResponseEntity<String> updatePost(@RequestParam Integer postId, @RequestParam String title, @RequestParam String postCategory, @RequestParam String content) {
        postService.update(postId, title, postCategory, content);
        return ResponseEntity.ok("글이 성공적으로 수정되었습니다.");
    }

    // 이미지 업로드 (미리보기)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/upload/image")
    public ResponseEntity<?> imageUpload(@RequestParam("image") MultipartFile imageFile) throws IOException {
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
    public ResponseEntity<String> checkLogin() {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
    }
}
