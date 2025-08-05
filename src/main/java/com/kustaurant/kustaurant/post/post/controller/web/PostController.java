package com.kustaurant.kustaurant.post.post.controller.web;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // 1. 게시글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/posts")
    public ResponseEntity<String> createPost(
            @RequestBody PostRequest postRequest,
            @AuthUser AuthUserInfo user
    ) {
        postService.create(postRequest, user.id());
        return ResponseEntity.ok("글이 성공적으로 저장되었습니다.");
    }

    // 2. 게시글 수정
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Integer postId,
            @RequestBody PostRequest req,
            @AuthUser AuthUserInfo user
    ) {
        postService.update(postId, req,user.id());
        return ResponseEntity.ok("글이 성공적으로 수정되었습니다.");
    }

    // 3. 게시글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Integer postId,
            @AuthUser AuthUserInfo user
    ) {
        postService.delete(postId,user.id());
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }
}
