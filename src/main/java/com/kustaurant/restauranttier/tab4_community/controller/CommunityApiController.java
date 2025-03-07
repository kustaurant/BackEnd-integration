package com.kustaurant.restauranttier.tab4_community.controller;


import com.kustaurant.restauranttier.common.UserService;
import com.kustaurant.restauranttier.common.apiUser.customAnno.JwtToken;
import com.kustaurant.restauranttier.common.exception.ErrorResponse;
import com.kustaurant.restauranttier.common.exception.exception.ServerException;
import com.kustaurant.restauranttier.tab4_community.dto.*;
import com.kustaurant.restauranttier.tab4_community.entity.*;
import com.kustaurant.restauranttier.tab4_community.etc.PostCategory;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
import com.kustaurant.restauranttier.tab4_community.service.PostApiCommentService;
import com.kustaurant.restauranttier.tab4_community.service.PostScrapApiService;
import com.kustaurant.restauranttier.tab4_community.service.PostApiService;
import com.kustaurant.restauranttier.tab4_community.service.StorageApiService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommunityApiController {
    private final PostApiService postApiService;
    private final PostApiCommentService postApiCommentService;
    private final PostScrapApiService postScrapApiService;
    private final StorageApiService storageApiService;
    private final PostApiRepository postApiRepository;
    private final UserService userService;

    // 커뮤니티 메인 화면
    @GetMapping("/api/v1/community/posts")
    @Operation(summary = "커뮤니티 메인화면의 게시글 리스트 불러오기", description = "게시판 종류와 페이지, 정렬 방법을 입력받고 해당 조건에 맞는 게시글 리스트가 반환됩니다, 현재 인기순으로 설정했을 때는 좋아요가 3이상인 게시글만 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 리스트를 반환하는데 성공하였습니다.", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "요청한 조건의 게시글을 찾을 수 없습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "400", description = "파라미터 값이 유효하지 않습니다.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})

    })
    public ResponseEntity<List<PostDTO>> community(
            @RequestParam(defaultValue = "all")
            @Parameter(example = "free", description = "게시판 종류입니다. (all:전체, free:자유게시판, column:칼럼게시판, suggestion:건의게시판)")
            String postCategory,
            @Parameter(example = "0", description = "게시글은 페이지 단위로 불러올 수 있고, 한 페이지에 10개의 게시글이 담겨있습니다. 페이지 인덱스는 0부터 시작합니다.")
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "recent")
            @Parameter(example = "recent", description = "게시글의 정렬 방법입니다. (recent:최신순, popular:인기순)")
            String sort,
            @RequestParam(defaultValue = "html")
            @Parameter(example = "text", description = "반환되는 게시글 내용의 타입입니다. (html,text)")
            String postBodyType
            ,@JwtToken @Parameter(hidden = true) Integer userId
    ) {
        // Enum으로 변환하고 한글 이름 추출
        PostCategory categoryEnum = PostCategory.fromStringToEnum(postCategory);
        String koreanCategory = categoryEnum.getKoreanName();
        Page<PostDTO> paging = postApiService.getPosts(page,sort,koreanCategory,postBodyType);

        return ResponseEntity.ok(paging.getContent());
    }

    // 커뮤니티 게시글 상세 화면
    @GetMapping("/api/v1/community/{postId}")
    @Operation(summary = "게시글 상세 화면", description = "게시글 ID를 받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> post(@PathVariable @Parameter(description = "게시글 id", example = "69") Integer postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        postApiService.validatePostId(postId);
        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(postId);
        postApiService.increaseVisitCount(post);
        return ResponseEntity.ok(postApiCommentService.createPostDTOWithFlags(post,user));
    }

    @GetMapping("/api/v1/community/ranking")
    @Operation(summary = "커뮤니티 메인의 랭킹 탭에서 유저 랭킹 불러오기", description = "평가 수 기반의 유저 랭킹을 반환합니다. 분기순, 최신순으로 랭킹을 산정할 수 있습니다. 평가를 1개 이상 한 유저들은 모두 랭킹이 매겨집니다.")
    @ApiResponse(responseCode = "200", description = "유저 랭킹을 반환하는데 성공하였습니다", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))})
    @ApiResponse(responseCode = "404", description = "sort 파라미터 값이 잘못되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public List<UserDTO> ranking(@RequestParam @Parameter(description = "랭킹 산정 기준입니다. 분기순:quarterly, 최신순:cumulative", example = "cumulative") String sort) {

        List<UserDTO> userList =  postApiService.getUserListforRanking(sort);
        return userList;
    }

    // 댓글 or 대댓글 생성
    @PostMapping("/api/v1/auth/community/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성 (생성한 댓글 반환)", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 생성한 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 혹은 대댓글 생성이 완료되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostCommentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.kustaurant.restauranttier.common.exception.ErrorResponse.class)))
    })
    public ResponseEntity<PostCommentDTO> postCommentCreateReturnCommentList(
            @RequestParam(name = "content", defaultValue = "")
            @Parameter(description = "생성할 댓글의 내용입니다. 최소 1자에서 최대 10,000자까지 입력 가능합니다.", example = "이 게시글에 대해 궁금한 점이 있습니다.")
            String content,

            @RequestParam(name = "postId")
            @Parameter(description = "댓글을 추가할 게시글의 ID입니다.", example = "123")
            String postId,

            @RequestParam(name = "parentCommentId", defaultValue = "")
            @Parameter(description = "대댓글을 작성할 경우, 부모 댓글의 ID를 입력합니다. 이 값이 비어 있으면 일반 댓글로 처리됩니다.", example = "456")
            String parentCommentId,

            @JwtToken
            @Parameter(hidden = true)
            Integer userId) {
        User user = userService.findUserById(userId);
        PostComment postComment = postApiCommentService.createComment(content, postId, userId);
        // 대댓글 일 경우 부모 댓글과 관계 매핑
        if (!parentCommentId.isEmpty()) {
            postApiCommentService.processParentComment(postComment, parentCommentId);
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postApiCommentService.createPostCommentDTOWithFlags(postComment, user));
    }

    // 댓글 or 대댓글 생성
    @PostMapping("/api/v2/auth/community/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성 (전체 댓글 목록 반환)", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 전체 댓글 목록을 반환합니다")
    public ResponseEntity<List<PostCommentDTO>> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "")
            @Parameter(description = "생성할 댓글의 내용입니다. 최소 1자에서 최대 10,000자까지 입력 가능합니다.", example = "이 게시글에 대해 궁금한 점이 있습니다.")
            String content,

            @RequestParam(name = "postId")
            @Parameter(description = "댓글을 추가할 게시글의 ID입니다.", example = "123")
            String postId,

            @RequestParam(name = "parentCommentId", defaultValue = "")
            @Parameter(description = "대댓글을 작성할 경우, 부모 댓글의 ID를 입력합니다. 이 값이 비어 있으면 일반 댓글로 처리됩니다.", example = "456")
            String parentCommentId,

            @JwtToken
            @Parameter(hidden = true)
            Integer userId) {
        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(Integer.valueOf(postId));
        PostComment postComment = postApiCommentService.createComment(content, postId, userId);
        // 대댓글 일 경우 부모 댓글과 관계 매핑
        if (!parentCommentId.isEmpty()) {
            postApiCommentService.processParentComment(postComment, parentCommentId);
        }
        List<PostCommentDTO> postCommentDTOs = postApiCommentService.getPostCommentDTOs(post, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postCommentDTOs);
    }


    @DeleteMapping("/api/v1/auth/community/{postId}")
    @Transactional
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 입력받고 해당 게시글을 삭제합니다. 삭제가 완료되면 204 상태 코드를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "해당 게시글 id의 게시글을 삭제 완료하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음 (삭제불가)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 게시글을 삭제할 권한이 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 삭제에 실패하였습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> postDelete(@PathVariable Integer postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        postApiService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }


    // 댓글, 대댓글 삭제
    @DeleteMapping("/api/v1/auth/community/comment/{commentId}")
    @Transactional
    @Operation(summary = "댓글 삭제", description = "댓글 ID를 입력받고 해당 댓글 및 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제에 성공하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 id의 댓글이 존재하지 않습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> commentDelete(
            @PathVariable
            @Parameter(description = "삭제할 댓글의 ID입니다.", example = "123")
            Integer commentId,
            @JwtToken
            @Parameter(hidden = true)
            Integer userId) {
        postApiCommentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // 게시글 스크랩
    @PostMapping("/api/v1/auth/community/{postId}/scraps")
    @Operation(summary = "게시글 스크랩", description = "게시글 ID를 입력받아 스크랩을 생성하거나 해제합니다. \n\nstatus와 현재 게시글의 스크랩 수를 반환합니다. \n\n처음 스크랩을 누르면 스크랩이 처리되고 status 값으로 1이 반환되며, 이미 스크랩된 상태였으면 해제되면서 status 값으로 0이 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 스크랩 처리가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ScrapToggleDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ScrapToggleDTO> postScrap(@PathVariable Integer postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(postId);
        int status = postScrapApiService.scrapCreateOrDelete(post, user); // 1 또는 0 반환
        ScrapToggleDTO scrapToggleDTO = new ScrapToggleDTO(post.getPostScrapList().size(), status);
        return ResponseEntity.ok(scrapToggleDTO);
    }

    // 게시글 좋아요 생성
    @PostMapping("/api/v1/auth/community/{postId}/likes")
    @Operation(summary = "게시글 좋아요 토글 버튼", description = "게시글 ID를 입력받아 좋아요를 생성하거나 해제합니다. \n\nstatus와 현재 게시글의 좋아요 수를 반환합니다. \n\n처음 좋아요를 누르면 좋아요가 처리되고 status 값으로 1이 반환되며, 이미 좋아요된 상태였으면 해제되면서 status 값으로 0이 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 처리가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikeOrDislikeDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 게시글이 존재하지 않습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LikeOrDislikeDTO> postLikeCreate(@PathVariable Integer postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(postId);
        int status = postApiService.likeCreateOrDelete(post, user); // 1 또는 0 반환
        LikeOrDislikeDTO likeOrDislikeDTO = new LikeOrDislikeDTO(post.getLikeUserList().size(), status);
        return ResponseEntity.ok(likeOrDislikeDTO);
    }


    // 댓글 좋아요/싫어요 처리
    @PostMapping("/api/v1/auth/community/comments/{commentId}/{action}")
    @Operation(summary = "댓글 좋아요/싫어요", description = "댓글 ID를 입력받아 댓글에 대한 좋아요 또는 싫어요를 토글합니다. \n\n 댓글에 대한 유저의 현재 상태를 나타내는 commentLikeStatus와 좋아요, 싫어요 개수를 반환합니다. \n\n commentLikeStatus는 1 (좋아요), -1 (싫어요), 0 (아무것도 아님) 값으로 분류됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "처리가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CommentLikeDislikeDTO.class))),
            @ApiResponse(responseCode = "404", description = "해당 ID의 댓글을 찾을 수 없습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommentLikeDislikeDTO> toggleCommentLikeOrDislike(@PathVariable @Parameter(description = "댓글 id입니다", example = "30") Integer commentId,
                                                                            @PathVariable @Parameter(description = "좋아요는 likes, 싫어요는 dislikes로 값을 설정합니다.", example = "likes") String action,
                                                                            @JwtToken @Parameter(hidden = true) Integer userId) {
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentId);
        User user = userService.findUserById(userId);
        int commentLikeStatus = postApiCommentService.toggleCommentLikeOrDislike(action, postComment,user);
        CommentLikeDislikeDTO responseDTO = CommentLikeDislikeDTO.toCommentLikeDislikeDTO(postComment,commentLikeStatus);
        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/api/v1/auth/community/posts/create")
    @Operation(summary = "게시글 생성", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 생성합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - postCategory: 필수.\n\n" +
            "   - content: 필수.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 생성되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))),
            @ApiResponse(responseCode = "500", description = "게시글 생성에 오류가 발생했습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> postCreate(
            @ModelAttribute PostUpdateDTO postUpdateDTO,
            @JwtToken @Parameter(hidden = true) Integer userId
    ) {
        try {
            User user = userService.findUserById(userId);
            Post post = new Post(postUpdateDTO.getTitle(), postUpdateDTO.getContent(), postUpdateDTO.getPostCategory(), "ACTIVE", LocalDateTime.now());
            postApiService.create(post, user);
            postApiRepository.save(post);
            return ResponseEntity.ok(PostDTO.convertPostToPostDTO(post));
        } catch (Exception e) {
            throw new ServerException("게시글 생성 중 서버 오류가 발생했습니다.", e);
        }
    }

    // 이미지 업로드
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/v1/auth/community/posts/image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUplodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "파일 이미지가 없거나 유효하지 않습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "게시글 작성 중 이미지 업로드", description = "게시글 작성화면에서 이미지를 업로드합니다")
    public ResponseEntity<?> imageUpload(@RequestParam("image") MultipartFile imageFile) throws IOException {

        try {
            String imageUrl = storageApiService.storeImage(imageFile);
            return ResponseEntity.ok(new ImageUplodeDTO(imageUrl));
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 이미지가 유효하지 않습니다");
        }
    }


    @PatchMapping("/api/v1/auth/community/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 수정합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - postCategory: 필수.\n\n" +
            "   - content: 필수.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정이 완료되었습니다."),
            @ApiResponse(responseCode = "500", description = "게시글 수정 중 서버에러가 발생했습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<String> postUpdate(
            @PathVariable @Parameter(description = "수정할 게시글의 ID입니다.", example = "123") String postId,
            @ModelAttribute PostUpdateDTO postUpdateDTO,
            @JwtToken @Parameter(hidden = true) Integer userId
    ) {
        try {
            Post post = postApiService.getPost(Integer.valueOf(postId));
            postApiService.updatePost(postUpdateDTO, post);
            postApiRepository.save(post);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ServerException("게시글 수정 중 서버 오류가 발생했습니다.", e);
        }
    }
}
