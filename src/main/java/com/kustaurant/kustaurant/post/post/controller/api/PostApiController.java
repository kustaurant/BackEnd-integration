package com.kustaurant.kustaurant.post.post.controller.api;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import com.kustaurant.kustaurant.post.post.controller.request.PostRequest;
import com.kustaurant.kustaurant.post.post.controller.response.ImageUplodeResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.service.S3Service;
import com.kustaurant.kustaurant.post.post.service.PostService;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PostApiController {
    private final PostService postService;
    private final S3Service S3Service;
    private final UserService userService;

    // 1. 게시글 생성
    @PostMapping("/api/v1/auth/community/posts/create")
    @Operation(summary = "게시글 생성", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 생성합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - category: 필수.\n\n" +
            "   - content: 필수.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 생성되었습니다", content = @Content(schema = @Schema(implementation = PostDetailResponse.class))),
            @ApiResponse(responseCode = "500", description = "게시글 생성에 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDetailResponse> postCreate(
            @RequestBody PostRequest req,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        Post post = postService.create(req, user.id());
        User writer = userService.getUserById(user.id());
        URI location = URI.create("/posts/" + post.getId());
        return ResponseEntity.created(location).body(PostDetailResponse.from(post,writer));
    }

    // 2. 게시글 수정
    @PatchMapping("/api/v1/auth/community/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 수정합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - category: 필수.\n\n" +
            "   - content: 필수.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정이 완료되었습니다."),
            @ApiResponse(responseCode = "500", description = "게시글 수정 중 서버에러가 발생했습니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<String> postUpdate(
            @PathVariable @Parameter(description = "수정할 게시글 ID", example = "123") Integer postId,
            @RequestBody PostRequest req,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        postService.update(postId, req,user.id());
        return ResponseEntity.ok().build();
    }

    // 3. 게시글 삭제
    @DeleteMapping("/api/v1/auth/community/{postId}")
    @Transactional
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 입력받고 해당 게시글을 삭제합니다. 삭제가 완료되면 204 상태 코드를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "해당 게시글 id의 게시글을 삭제 완료하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음 (삭제불가)", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 게시글을 삭제할 권한이 없습니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 삭제에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> postDelete(
            @PathVariable Integer postId,
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        postService.delete(postId, user.id());
        return ResponseEntity.noContent().build();
    }

    // 4. 이미지 업로드
    @PostMapping("/api/v1/auth/community/posts/image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUplodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "파일 이미지가 없거나 유효하지 않습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @Operation(summary = "게시글 작성 중 이미지 업로드", description = "게시글 작성화면에서 이미지를 업로드합니다")
    public ResponseEntity<?> imageUpload(
            @RequestParam("image") MultipartFile imageFile
    ) {
        try {
            String imageUrl = S3Service.storeImage(imageFile);
            return ResponseEntity.ok(new ImageUplodeResponse(imageUrl));
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 이미지가 유효하지 않습니다");
        }
    }

}
