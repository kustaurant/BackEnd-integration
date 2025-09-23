package com.kustaurant.mainapp.post.post.controller;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.post.post.controller.request.PostRequest;
import com.kustaurant.mainapp.post.post.service.PostService;
import com.kustaurant.mainapp.post.post.service.PostS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostS3Service storageService;

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

    // 3. 게시글 수정 완료
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PutMapping("/api/posts/{postId}")
    public ResponseEntity<String> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest req,
            @AuthUser AuthUserInfo user
    ) {
        postService.update(postId, req,user.id());
        return ResponseEntity.ok("글이 성공적으로 수정되었습니다.");
    }

    // 4. 게시글 삭제
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            @AuthUser AuthUserInfo user
    ) {
        postService.delete(postId,user.id());
        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
    }

    // 5. 이미지 업로드 (미리보기)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/images")
    public ResponseEntity<Map<String, Object>> imageUpload(
            @RequestParam("image") MultipartFile imageFile
    ) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "파일이 없습니다."));
        }

        try {
            String imageUrl = storageService.storeImage(imageFile);
            String thumbnailUrl = imageUrl; // 실제로는 썸네일 URL을 생성하거나 처리해야 할 수 있습니다.

            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("thumbnailPath", thumbnailUrl);
            fileInfo.put("fullPath", imageUrl);
            fileInfo.put("orgFilename", imageFile.getOriginalFilename());

            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "이미지 업로드 실패"));
        }
    }
}
