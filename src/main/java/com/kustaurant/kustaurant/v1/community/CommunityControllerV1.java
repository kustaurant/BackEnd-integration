package com.kustaurant.kustaurant.v1.community;

import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.ApiStatusException;
import com.kustaurant.kustaurant.post.comment.service.PostCommentService;
import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
import com.kustaurant.kustaurant.post.community.controller.response.PostListResponse;
import com.kustaurant.kustaurant.post.community.service.PostQueryService;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.service.PostS3Service;
import com.kustaurant.kustaurant.post.post.service.PostScrapService;
import com.kustaurant.kustaurant.post.post.service.PostService;
import com.kustaurant.kustaurant.user.rank.controller.response.UserRankResponse;
import com.kustaurant.kustaurant.user.rank.domain.RankingSortOption;
import com.kustaurant.kustaurant.user.rank.service.RankingService;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.v1.common.JwtToken;
import com.kustaurant.kustaurant.v1.community.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommunityControllerV1 {
    private final PostService postService;
    private final PostCommentService postCommentService;
    private final PostScrapService postScrapService;
    private final PostS3Service storageService;
    private final UserService userService;

    private final PostQueryService postQueryService;
    private final RankingService rankingService;
    private final CommunityCompatMapper compatMapper;

    // 1. 커뮤니티 메인 게시글 목록 화면
    @GetMapping("/api/v1/community/posts")
    public ResponseEntity<List<PostDTO>> community(
            @RequestParam(defaultValue = "all") String postCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "html") String postBodyType,
            @JwtToken Long userId
    ) {
        PostCategory category = PostCategory.from(postCategory);
        SortOption sortOption = mapSort(sort);
        Page<PostListResponse> v2 = postQueryService.getPostList(page, category, sortOption);

        return ResponseEntity.ok(compatMapper.toLegacyPostList(v2.getContent()));
    }

    // 2. 커뮤니티 게시글 상세 화면
    @GetMapping("/api/v1/community/{postId}")
    public ResponseEntity<PostDTO> post(
            @PathVariable Long postId,
            @JwtToken Long userId
    ) {
//        PostDetailResponse v2res = postQueryService.getPostDetail(postId, userId);
//        PostDTO v1res = PostDTO.from(v2res);
//        return ResponseEntity.ok(v1res);
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 3. 유저 랭킹
    @GetMapping("/api/v1/community/ranking")
    public List<UserDTO> ranking(
            @RequestParam String sort
    ) {
        RankingSortOption option = parseSort(sort);
        List<UserRankResponse> v2 = rankingService.getTop100(option);

        return compatMapper.toLegacyUserRankList(v2);
    }

    // 4. 댓글 or 대댓글 생성
    @PostMapping("/api/v1/auth/community/comments")
    public ResponseEntity<PostCommentDTO> postCommentCreateReturnCommentList(
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "parentCommentId", defaultValue = "") String parentCommentId,
            @JwtToken Long userId
    ) {
//        User user = userService.findUserById(userId);
//        PostComment postComment = postCommentService.createComment(content, postId, userId);
//        // 대댓글 일 경우 부모 댓글과 관계 매핑
//        if (!parentCommentId.isEmpty()) {
//            postCommentService.processParentComment(postComment, parentCommentId);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(postCommentService.createPostCommentDTOWithFlags(postComment, user));
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 5. 댓글 or 대댓글 생성
    @PostMapping("/api/v2/auth/community/comments")
    public ResponseEntity<List<PostCommentDTO>> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "parentCommentId", defaultValue = "") String parentCommentId,
            @JwtToken Long userId
    ) {
//        User user = userService.findUserById(userId);
//        Post post = postService.getPost(Integer.valueOf(postId));
//        PostComment postComment = postCommentService.createComment(content, postId, userId);
//        // 대댓글 일 경우 부모 댓글과 관계 매핑
//        if (!parentCommentId.isEmpty()) {
//            postCommentService.processParentComment(postComment, parentCommentId);
//        }
//        List<PostCommentDTO> postCommentDTOs = postCommentService.getPostCommentDTOs(post, user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(postCommentDTOs);
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 6. 게시글 삭제
    @DeleteMapping("/api/v1/auth/community/{postId}")
    public ResponseEntity<Void> postDelete(
            @PathVariable Integer postId,
            @JwtToken Long userId
    ) {
//        postService.deletePost(postId, userId);
//        return ResponseEntity.noContent().build();
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }


    // 7. 댓글, 대댓글 삭제
    @DeleteMapping("/api/v1/auth/community/comment/{commentId}")
    public ResponseEntity<Map<String, Object>> commentDelete(
            @PathVariable Integer commentId,
            @JwtToken Long userId
    ) {
//        postCommentService.deleteComment(commentId, userId);
//        return ResponseEntity.noContent().build();
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 8. 게시글 스크랩
    @PostMapping("/api/v1/auth/community/{postId}/scraps")
    public ResponseEntity<ScrapToggleDTO> postScrap(
            @PathVariable Integer postId,
            @JwtToken Long userId
    ) {
//        User user = userService.findUserById(userId);
//        Post post = postService.getPost(postId);
//        int status = postScrapService.scrapCreateOrDelete(post, user); // 1 또는 0 반환
//        ScrapToggleDTO scrapToggleDTO = new ScrapToggleDTO(post.getPostScrapList().size(), status);
//        return ResponseEntity.ok(scrapToggleDTO);
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 9. 게시글 좋아요 생성
    @PostMapping("/api/v1/auth/community/{postId}/likes")
    public ResponseEntity<LikeOrDislikeDTO> postLikeCreate(
            @PathVariable Integer postId,
            @JwtToken Long userId
    ) {
//        User user = userService.findUserById(userId);
//        Post post = postService.getPost(postId);
//        int status = postService.likeCreateOrDelete(post, user); // 1 또는 0 반환
//        LikeOrDislikeDTO likeOrDislikeDTO = new LikeOrDislikeDTO(post.getLikeUserList().size(), status);
//        return ResponseEntity.ok(likeOrDislikeDTO);
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }


    // 10. 댓글 좋아요/싫어요 처리
    @PostMapping("/api/v1/auth/community/comments/{commentId}/{action}")
    public ResponseEntity<CommentLikeDislikeDTO> toggleCommentLikeOrDislike(
            @PathVariable Integer commentId,
            @PathVariable String action,
            @JwtToken Long userId
    ) {
//        PostComment postComment = postCommentService.getPostCommentByCommentId(commentId);
//        User user = userService.findUserById(userId);
//        int commentLikeStatus = postCommentService.toggleCommentLikeOrDislike(action, postComment,user);
//        CommentLikeDislikeDTO responseDTO = CommentLikeDislikeDTO.toCommentLikeDislikeDTO(postComment,commentLikeStatus);
//        return ResponseEntity.ok(responseDTO);
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 11. 포스트 생성
    @PostMapping("/api/v1/auth/community/posts/create")
    public ResponseEntity<PostDTO> postCreate(
            @ModelAttribute PostUpdateDTO postUpdateDTO,
            @JwtToken Long userId
    ) {
//        try {
//            User user = userService.findUserById(userId);
//            Post post = new Post(postUpdateDTO.getTitle(), postUpdateDTO.getContent(), postUpdateDTO.getPostCategory(), "ACTIVE", LocalDateTime.now());
//            postService.create(post, user);
//            postApiRepository.save(post);
//            return ResponseEntity.ok(PostDTO.convertPostToPostDTO(post));
//        } catch (Exception e) {
//            throw new ServerException("게시글 생성 중 서버 오류가 발생했습니다.", e);
//        }
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 12. 이미지 업로드
    @PostMapping("/api/v1/auth/community/posts/image")
    public ResponseEntity<?> imageUpload(
            @RequestParam("image") MultipartFile imageFile
    ) {
//        try {
//            String imageUrl = storageService.storeImage(imageFile);
//            return ResponseEntity.ok(new ImageUplodeDTO(imageUrl));
//        } catch (Exception e) {
//            throw new IllegalArgumentException("파일 이미지가 유효하지 않습니다");
//        }

        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    // 13. 게시글 수정
    @PatchMapping("/api/v1/auth/community/posts/{postId}")
    public ResponseEntity<String> postUpdate(
            @PathVariable String postId,
            @ModelAttribute PostUpdateDTO postUpdateDTO,
            @JwtToken Long userId
    ) {
//        try {
//            Post post = postService.getPost(Integer.valueOf(postId));
//            postService.updatePost(postUpdateDTO, post);
//            postApiRepository.save(post);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            throw new ServerException("게시글 수정 중 서버 오류가 발생했습니다.", e);
//        }
        throw new ApiStatusException(ErrorCode.SERVICE_UNAVAILABLE);
    }

    private RankingSortOption parseSort(String sort) {
        if (sort == null) return RankingSortOption.CUMULATIVE;
        try {
            return RankingSortOption.valueOf(sort.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return RankingSortOption.CUMULATIVE;
        }
    }

    private SortOption mapSort(String s) {
        if (s == null) return SortOption.LATEST;
        String v = s.trim().toLowerCase();
        if (v.equals("recent")) return SortOption.LATEST;
        if (v.equals("popular")) return SortOption.POPULARITY;
        return SortOption.LATEST;
    }
}