package com.kustaurant.restauranttier.tab4_community.controller;


import com.kustaurant.restauranttier.common.UserService;
import com.kustaurant.restauranttier.common.apiUser.customAnno.JwtToken;
import com.kustaurant.restauranttier.common.exception.ErrorResponse;
import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ServerException;
import com.kustaurant.restauranttier.tab4_community.dto.*;
import com.kustaurant.restauranttier.tab4_community.entity.*;
import com.kustaurant.restauranttier.tab4_community.etc.PostCategory;
import com.kustaurant.restauranttier.tab4_community.repository.PostCommentApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostPhotoApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostScrapApiRepository;
import com.kustaurant.restauranttier.tab4_community.service.PostApiCommentService;
import com.kustaurant.restauranttier.tab4_community.service.PostScrapApiService;
import com.kustaurant.restauranttier.tab4_community.service.PostApiService;
import com.kustaurant.restauranttier.tab4_community.service.StorageApiService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class CommunityApiController {
    private final PostApiService postApiService;
    private final PostApiCommentService postApiCommentService;
    private final PostScrapApiService postScrapApiService;
    private final StorageApiService storageApiService;
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostPhotoApiRepository postPhotoApiRepository;
    private final UserRepository userRepository;
    private final PostCommentApiRepository postCommentApiRepository;
    private final PostApiRepository postApiRepository;

    private final UserService userService;

    // 커뮤니티 메인 화면
    @GetMapping("/community/posts")
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
            String sort
    ) {
        // Enum으로 변환하고 한글 이름 추출
        PostCategory categoryEnum = PostCategory.fromStringToEnum(postCategory);
        String koreanCategory = categoryEnum.getKoreanName();

        Page<PostDTO> paging;
        if (categoryEnum == PostCategory.ALL) {
            paging = postApiService.getList(page, sort);
        } else {
            paging = postApiService.getListByPostCategory(koreanCategory, page, sort);
        }


        return ResponseEntity.ok(paging.getContent());
    }

    // 커뮤니티 게시글 상세 화면
    @GetMapping("/community/{postId}")
    @Operation(summary = "게시글 상세 화면", description = "게시글 ID와 해당 게시물의 댓글의 정렬 방법을 입력받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> post(@PathVariable @Parameter(description = "게시글 id", example = "69") Integer postId) {
        // 잘못된 요청을 처리 (예: 음수 ID)
        if (postId <= 0) {
            throw new IllegalArgumentException("잘못된 게시글 ID입니다.");
        }

        // 게시글 조회
        Post post = postApiService.getPost(postId);
        List<PostComment> postCommentList = post.getPostCommentList();
        postCommentList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());
        // 조회수 증가
        postApiService.increaseVisitCount(post);

        // DTO로 변환
        PostDTO postDTO = PostDTO.fromEntity(post);

        // 성공적으로 게시글 반환
        return ResponseEntity.ok(postDTO);
    }

    @GetMapping("/community/ranking")
    @Operation(summary = "커뮤니티 메인의 랭킹 탭에서 유저 랭킹 불러오기", description = "평가 수 기반의 유저 랭킹을 반환합니다. 분기순, 최신순으로 랭킹을 산정할 수 있습니다. 평가를 1개 이상 한 유저들은 모두 랭킹이 매겨집니다.")
    @ApiResponse(responseCode = "200", description = "유저 랭킹을 반환하는데 성공하였습니다", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserDTO.class)))})
    @ApiResponse(responseCode = "404", description = "sort 파라미터 값이 잘못되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})
    public List<UserDTO> ranking(@RequestParam @Parameter(description = "랭킹 산정 기준입니다. 분기순:quarterly, 최신순:cumulative", example = "cumulative") String sort) {
        if ("cumulative".equals(sort)) {
            // 누적 기준으로 유저 리스트 가져오기 (정렬된 상태)
            List<User> userList = userRepository.findUsersWithEvaluationCountDescending();
            // 유저의 평가수, 랭킹 첨부하기
            return calculateRank(userList);
        } else if ("quarterly".equals(sort)) {
            // 현재 날짜를 기준으로 연도와 분기 계산
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentQuarter = getCurrentQuarter(now);

            // 특정 분기의 평가 데이터를 기준으로 유저 리스트 가져오기
            List<User> userList = userRepository.findUsersByEvaluationCountForQuarter(currentYear, currentQuarter);
            // 분기별 순위 리스트 계산
            return calculateRankForQuarter(userList, currentYear, currentQuarter);
        } else {
            throw new OptionalNotExistException("sort값이 잘못 입력되었습니다.");
        }
    }

    // 댓글 or 대댓글 생성
    @PostMapping("/auth/community/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 생성한 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 혹은 대댓글 생성이 완료되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostCommentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.kustaurant.restauranttier.common.exception.ErrorResponse.class)))
    })
    public ResponseEntity<PostCommentDTO> postCommentCreate(
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
        Integer postIdInt = Integer.valueOf(postId);

        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(postIdInt);
        PostComment postComment = new PostComment(content, "ACTIVE", LocalDateTime.now(), post, user);
        PostComment savedPostComment = postCommentApiRepository.save(postComment);

        // 대댓글이면 부모 관계 매핑하기
        if (!parentCommentId.isEmpty()) {
            PostComment parentComment = postApiCommentService.getPostCommentByCommentId(Integer.valueOf(parentCommentId));
            savedPostComment.setParentComment(parentComment);
            parentComment.getRepliesList().add(savedPostComment);
            postApiCommentService.replyCreate(user, savedPostComment);
            postCommentApiRepository.save(parentComment);
        }
        // 댓글이면 post와 연결하면서 저장
        else {
            postApiCommentService.create(post, user, savedPostComment);
        }
        postCommentApiRepository.save(savedPostComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(PostCommentDTO.fromEntity(savedPostComment));
    }

    @DeleteMapping("/auth/community/{postId}")
    @Transactional
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 입력받고 해당 게시글을 삭제합니다. 삭제가 완료되면 204 상태 코드를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "해당 게시글 id의 게시글을 삭제 완료하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음 (삭제불가)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 게시글을 삭제할 권한이 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 삭제에 실패하였습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> postDelete(@PathVariable Integer postId, @JwtToken @Parameter(hidden = true) Integer userId) {

        Post post = postApiService.getPost(postId);


        if (!post.getUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("해당 게시글에 대한 권한이 없습니다.");
        }

        // 게시글과 관련된 댓글들 상태 변경
        List<PostComment> comments = post.getPostCommentList();
        for (PostComment comment : comments) {
            comment.setStatus("DELETED");
            // 대댓글 삭제
            for (PostComment reply : comment.getRepliesList()) {
                reply.setStatus("DELETED");
            }
        }

        // 게시글과 관련된 스크랩 정보 삭제
        List<PostScrap> scraps = post.getPostScrapList();
        postScrapApiRepository.deleteAll(scraps);

        // 게시글과 관련된 사진 삭제
        List<PostPhoto> existingPhotos = post.getPostPhotoList();
        if (existingPhotos != null) {
            postPhotoApiRepository.deleteAll(existingPhotos);
            post.setPostPhotoList(null); // 기존 리스트 연결 해제
        }

        // 게시글 상태를 삭제로 변경
        post.setStatus("DELETED");

        // 204 No Content 반환 (삭제 성공)
        return ResponseEntity.noContent().build();
    }


    // 댓글, 대댓글 삭제
    @DeleteMapping("/auth/community/comment/{commentId}")
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
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentId);
        postComment.setStatus("DELETED");

        List<PostComment> repliesList = postComment.getRepliesList();
        // 해당 댓글에 대한 대댓글이 존재하면 삭제
        if (!repliesList.isEmpty()) {
            for (PostComment reply : repliesList) {
                if (!reply.getStatus().equals("DELETED")) {
                    reply.setStatus("DELETED");
                }

            }

        }
        return ResponseEntity.noContent().build();
    }

    // 게시글 스크랩
    @PostMapping("/auth/community/{postId}/scraps")
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
    @PostMapping("/auth/community/{postId}/likes")
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
    @PostMapping("/auth/community/comments/{commentId}/{action}")
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

        int commentLikeStatus;
        if ("likes".equals(action)) {
            commentLikeStatus = postApiCommentService.likeCreateOrDelete(postComment, user);
        } else if ("dislikes".equals(action)) {
            commentLikeStatus = postApiCommentService.dislikeCreateOrDelete(postComment, user);
        } else {
            throw new IllegalArgumentException("action 값이 유효하지 않습니다.");
        }

        CommentLikeDislikeDTO responseDTO = new CommentLikeDislikeDTO(
                postComment.getLikeCount(),
                postComment.getDislikeUserList().size(),
                commentLikeStatus
        );

        return ResponseEntity.ok(responseDTO);
    }


    @PostMapping("/auth/community/posts/create")
    @Operation(summary = "게시글 생성", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 생성합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - postCategory: 필수.\n\n" +
            "   - content: 필수.\n\n" +
            "   - imageFile: 없어도 됩니다.\n\n")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글이 생성되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))),
            @ApiResponse(responseCode = "500", description = "게시글 생성에 오류가 발생했습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> postCreate(
            @ModelAttribute PostUpdateDTO postUpdateDTO,
            @JwtToken @Parameter(hidden = true) Integer userId
    ) {
        try {
            // 유저 정보 가져오기
            User user = userService.findUserById(userId);

            // 게시글 객체 생성
            Post post = new Post(postUpdateDTO.getTitle(), postUpdateDTO.getContent(), postUpdateDTO.getPostCategory(), "ACTIVE", LocalDateTime.now());

            // 유저와 게시글의 연관관계 설정
            postApiService.create(post, user);

            // 이미지 파일 처리
            handleImageUpload(post, postUpdateDTO.getImageFile());

            // 게시글 저장
            postApiRepository.save(post);

            // 성공 응답 반환
            return ResponseEntity.ok(PostDTO.fromEntity(post));
        } catch (IOException e) {
            throw new ServerException("이미지 처리 과정에서 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new ServerException("게시글 생성 중 서버 오류가 발생했습니다.", e);
        }
    }

    // 이미지 업로드
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/auth/community/posts/image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드가 완료되었습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUplodeDTO.class))),
            @ApiResponse(responseCode = "400", description = "파일 이미지가 없거나 유효하지 않습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @Operation(summary = "게시글 작성 중 이미지 업로드", description = "게시글 작성화면에서 이미지를 업로드합니다")
    public ResponseEntity<?> imageUpload(@RequestParam("image") MultipartFile imageFile) throws IOException {

        try {
            // StorageService를 통해 이미지를 S3에 저장하고, URL을 받아옵니다.
            String imageUrl = storageApiService.storeImage(imageFile);


            return ResponseEntity.ok(new ImageUplodeDTO(imageUrl));
        } catch (Exception e) {
            throw new IllegalArgumentException("파일 이미지가 유효하지 않습니다");
        }
    }


    @PatchMapping("/auth/community/posts/{postId}")
    @Operation(summary = "게시글 수정", description = "게시글 제목, 카테고리, 내용, 이미지를 입력받아 게시글을 수정합니다.\n\n" +
            "- 요청 형식 보충 설명\n\n" +
            "   - title: 필수\n\n" +
            "   - postCategory: 필수.\n\n" +
            "   - content: 필수.\n\n" +
            "   - imageFile: 없어도 됩니다.\n\n")
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

            // DTO를 통해 받은 데이터로 수정
            if (postUpdateDTO.getTitle() != null) post.setPostTitle(postUpdateDTO.getTitle());
            if (postUpdateDTO.getPostCategory() != null) post.setPostCategory(postUpdateDTO.getPostCategory());
            if (postUpdateDTO.getContent() != null) post.setPostBody(postUpdateDTO.getContent());

            // 이미지 파일 처리
            handleImageUpload(post, postUpdateDTO.getImageFile());

            // 게시글 저장
            postApiRepository.save(post);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ServerException("이미지 처리 과정에서 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new ServerException("게시글 수정 중 서버 오류가 발생했습니다.", e);
        }
    }


    private int getCurrentQuarter(LocalDate date) {
        int month = date.getMonthValue();
        if (month >= 1 && month <= 3) {
            return 1;
        } else if (month >= 4 && month <= 6) {
            return 2;
        } else if (month >= 7 && month <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    private List<UserDTO> calculateRank(List<User> userList) {
        List<UserDTO> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user : userList) {
            int evaluationCount = user.getEvaluationList().size();
            UserDTO userDTO = UserDTO.fromEntity(user); // 필요한 정보를 UserDTO에 담음

            if (evaluationCount < prevCount) {
                i += countSame;
                userDTO.setRank(i);
                countSame = 1;
            } else {
                userDTO.setRank(i);
                countSame++;
            }

            rankList.add(userDTO);
            prevCount = evaluationCount;
        }
        return rankList;
    }

    private List<UserDTO> calculateRankForQuarter(List<User> userList, int year, int quarter) {
        List<UserDTO> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user : userList) {
            // 특정 분기의 평가 수 계산
            int evaluationCount = (int) user.getEvaluationList().stream()
                    .filter(e -> getYear(e.getCreatedAt()) == year && getQuarter(e.getCreatedAt()) == quarter)
                    .count();

            UserDTO userDTO = UserDTO.fromEntity(user); // 필요한 정보를 UserDTO에 담음
            userDTO.setEvaluationCount(evaluationCount); // 분기 내 평가 수를 설정

            if (evaluationCount < prevCount) {
                i += countSame;
                userDTO.setRank(i);
                countSame = 1;
            } else {
                userDTO.setRank(i);
                countSame++;
            }

            rankList.add(userDTO);
            prevCount = evaluationCount;
        }
        return rankList;
    }

    private int getYear(LocalDateTime dateTime) {
        return dateTime.getYear();
    }

    private int getQuarter(LocalDateTime dateTime) {
        int month = dateTime.getMonthValue();
        if (month <= 3) {
            return 1;
        } else if (month <= 6) {
            return 2;
        } else if (month <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    private void handleImageUpload(Post post, String imageUrl) throws IOException {
        if (imageUrl != null) {
            PostPhoto postPhoto = new PostPhoto(imageUrl, "ACTIVE");
            postPhoto.setPost(post);
            post.getPostPhotoList().clear();  // 기존 이미지를 제거하고 새로운 이미지를 추가
            post.getPostPhotoList().add(postPhoto);
            postPhotoApiRepository.save(postPhoto);
        }
    }
}
