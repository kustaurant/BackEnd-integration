package com.kustaurant.restauranttier.tab4_community.controller;


import com.kustaurant.restauranttier.common.UserService;
import com.kustaurant.restauranttier.common.apiUser.JwtToken;
import com.kustaurant.restauranttier.common.exception.ErrorResponse;
import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab4_community.dto.LikeOrDislikeDTO;
import com.kustaurant.restauranttier.tab4_community.dto.PostDTO;
import com.kustaurant.restauranttier.tab4_community.dto.UserDTO;
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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/api/v1/community")
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
    @GetMapping("/posts")
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
        if (categoryEnum == PostCategory.FREE) {
            paging = postApiService.getList(page, sort);
        } else {
            paging = postApiService.getListByPostCategory(koreanCategory, page, sort);
        }

        // 요청한 조건에 해당하는 게시글이 없는 경우 예외 발생
        if (paging.isEmpty()) {
            throw new OptionalNotExistException("요청한 조건에 해당하는 게시글이 없습니다.");
        }

        return ResponseEntity.ok(paging.getContent());
    }

    // 커뮤니티 게시글 상세 화면
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 화면", description = "게시글 ID와 해당 게시물의 댓글의 정렬 방법을 입력받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 반환 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 게시글 ID)", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버에서 오류가 발생했습니다", content = @Content(mediaType = "apllication/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PostDTO> post(@PathVariable @Parameter(description = "게시글 id", example = "69") Integer postId,
                                        @RequestParam(defaultValue = "recent")
                                        @Parameter(example = "recent", description = "정렬 종류입니다. (recent:최신순, popular:인기순)")
                                        String sort) {
        // 잘못된 요청을 처리 (예: 음수 ID)
        if (postId <= 0) {
            throw new IllegalArgumentException("잘못된 게시글 ID입니다.");
        }

        // 게시글 조회
        Post post = postApiService.getPost(postId);
        List<PostComment> postCommentList = post.getPostCommentList();

        if (sort.equals("recent")) {
            // createdAt으로 정렬 (최신순)
            postCommentList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());
        } else if (sort.equals("popular")) {
            // likeCount로 정렬 (인기순)
            postCommentList.sort(Comparator.comparing(PostComment::getLikeCount).reversed());
        } else {
            throw new IllegalArgumentException("sort 파라미터 값이 올바르지 않습니다.");
        }
        // 조회수 증가
        postApiService.increaseVisitCount(post);

        // DTO로 변환
        PostDTO postDTO = PostDTO.fromEntity(post);

        // 성공적으로 게시글 반환
        return ResponseEntity.ok(postDTO);
    }

    @GetMapping("/ranking")
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
    @PostMapping("/comments")
    @Operation(summary = "댓글 생성, 대댓글 생성", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다. 생성한 댓글을 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 혹은 대댓글 생성이 완료되었습니다", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostCommentDTO.class))}),
            @ApiResponse(responseCode = "404", description = "해당 postId의 게시글을 찾을 수 없습니다", content = @Content(mediaType = "application/json",schema = @Schema(implementation = com.kustaurant.restauranttier.common.exception.ErrorResponse.class)))
    })
    public ResponseEntity<PostCommentDTO> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "parentCommentId", defaultValue = "") String parentCommentId,
            @JwtToken @Parameter(hidden = true) Integer userId) {
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

    @DeleteMapping("/{postId}")
    @Transactional
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 입력받고 해당 게시글을 삭제합니다. 삭제가 완료되면 204 상태 코드를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "해당 게시글 id의 게시글을 삭제 완료하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 게시글을 찾을 수 없음 (삭제불가)", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "해당 게시글을 삭제할 권한이 없습니다.", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류로 인해 삭제에 실패하였습니다.", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class)))
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
    @DeleteMapping("/comment/{commentId}")
    @Transactional
    @Operation(summary = "댓글 삭제", description = "댓글 ID를 입력받고 해당 댓글 및 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제에 성공하였습니다.", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 id의 댓글이 존재하지 않습니다", content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> commentDelete(@PathVariable Integer commentId, @JwtToken @Parameter(hidden = true) Integer userId) {
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



    // 게시글 좋아요 생성
    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요", description = "게시글 ID를 입력받아 좋아요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 처리가 완료되었습니다", content = @Content),
            @ApiResponse(responseCode = "404", description = "해당 id의 게시글이 존재하지 않습니다", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LikeOrDislikeDTO> postLikeCreate(@PathVariable String postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        Integer postidInt = Integer.valueOf(postId);
        User user = userService.findUserById(userId);
        Post post = postApiService.getPost(postidInt);
        String status = postApiService.likeCreateOrDelete(post, user);
        // 응답 맵 구성
        LikeOrDislikeDTO likeOrDislikeDTO = new LikeOrDislikeDTO(post.getLikeUserList().size(),post.getDislikeUserList().size(),status);


        return ResponseEntity.ok(likeOrDislikeDTO);
    }

    // 게시글 싫어요 생성
    @PostMapping("/{postId}/dislikes")
    @Operation(summary = "게시글 싫어요", description = "게시글 ID를 입력받아 싫어요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dislike success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> postDislikeCreate(@PathVariable String postId, @JwtToken @Parameter(hidden = true) Integer userId) {
        Integer postidInt = Integer.valueOf(postId);

        User user = userService.findUserById(userId);
        ;
        Post post = postApiService.getPost(postidInt);
        Map<String, Object> response = postApiService.dislikeCreateOrDelete(post, user);
        response.put("dislikeCount", post.getDislikeUserList().size());
        response.put("likeCount", post.getLikeUserList().size());
        return ResponseEntity.ok(response);
    }

    // 게시글 스크랩 (구현완료)
    @PostMapping("/{postId}/scraps")
    @Operation(summary = "게시글 스크랩 ", description = "게시글 ID를 입력받아 스크랩을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "scrap success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> postScrap(@PathVariable String postId, Model model, @JwtToken @Parameter(hidden = true) Integer userId) {
        Integer postidInt = Integer.valueOf(postId);

        User user = userService.findUserById(userId);
        ;
        Post post = postApiService.getPost(postidInt);
        Map<String, Object> response = postScrapApiService.scrapCreateOrDelete(post, user);
        return ResponseEntity.ok(response);
    }

    // 게시글 검색 화면
    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "검색어를 입력받아 게시글 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "search success", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "no posts found", content = @Content)
    })
    public ResponseEntity<List<PostDTO>> search(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", defaultValue = "") String kw,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "전체") String postCategory) {

        Page<PostDTO> paging = this.postApiService.getList(page, sort, kw, postCategory);


        return ResponseEntity.ok(paging.getContent());
    }

    // 게시글 댓글 좋아요
    @PostMapping("/comments/{commentId}/likes")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "댓글 좋아요", description = "댓글 ID를 입력받아 좋아요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "like success", content = @Content),
            @ApiResponse(responseCode = "404", description = "comment not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable String commentId, @JwtToken @Parameter(hidden = true) Integer userId) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentIdInt);

        User user = userService.findUserById(userId);
        ;
        Map<String, Object> response = postApiCommentService.likeCreateOrDelete(postComment, user);
        response.put("totalLikeCount", postComment.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 게시글 댓글 싫어요
    @PostMapping("/comments/{commentId}/dislikes")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "댓글 싫어요", description = "댓글 ID를 입력받아 싫어요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dislike success", content = @Content),
            @ApiResponse(responseCode = "404", description = "comment not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> dislikeComment(@PathVariable String commentId, @JwtToken @Parameter(hidden = true) Integer userId) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentIdInt);

        User user = userService.findUserById(userId);
        ;
        Map<String, Object> response = postApiCommentService.dislikeCreateOrDelete(postComment, user);
        response.put("totalLikeCount", postComment.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 게시글 생성
    @PostMapping("/posts")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 생성 ", description = "게시글 제목,카테고리,내용,이미지를 입력받아 게시글을 생성합니다. 이미지 저장은 아직 미구현 상태입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create success", content = @Content),
            @ApiResponse(responseCode = "404", description = "user not found", content = @Content)
    })
    public ResponseEntity<PostDTO> postCreate(
            @RequestParam("title") String title,
            @RequestParam("postCategory") String postCategory,
            @RequestParam("content") String content,
            @RequestParam(value = "imgUrl", required = false) String imgUrl,
            @JwtToken Integer userId) throws IOException {

        // 게시글 객체 생성
        Post post = new Post(title, content, postCategory, "ACTIVE", LocalDateTime.now());

        User user = userService.findUserById(userId);
        ;
        postApiService.create(post, user);

//        // TinyMCE 컨텐츠에서 <img> 태그를 파싱
//        Document doc = Jsoup.parse(content);
//        Elements imgTags = doc.select("img");

//        // 이미지 파일 처리
//        PostPhoto postPhoto = new PostPhoto(imgUrl, "ACTIVE");
//        postPhoto.setPost(post); // 게시글과 이미지 연관관계 설정
//        post.getPostPhotoList().add(postPhoto); // post의 이미지 리스트에 추가
//        postPhotoApiRepository.save(postPhoto); // 이미지 정보 저장


        // 게시글 저장
        postApiRepository.save(post);

        return ResponseEntity.ok(PostDTO.fromEntity(post));
    }

    // 게시글 수정
    @PatchMapping("/posts/{postId}")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 수정 (미구현)", description = "게시글 ID와 내용을 입력받아 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<String> postUpdate(
            @PathVariable String postId,
            @RequestParam String title,
            @RequestParam String postCategory,
            @RequestParam String content,
            @JwtToken Integer userId
    ) {

        // TODO: 사진 업로드 구현
        Post post = postApiService.getPost(Integer.valueOf(postId));
        // 기존 연관된 사진 정보 삭제
        List<PostPhoto> existingPhotos = post.getPostPhotoList();
        if (existingPhotos != null) {
            postPhotoApiRepository.deleteAll(existingPhotos);
            post.setPostPhotoList(null); // 기존 리스트 연결 해제
        }

        // 새로운 사진 정보 처리 로직 (기존 로직 유지)
        List<PostPhoto> newPhotoList = new ArrayList<>();
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");
        for (Element img : imgTags) {
            String imgUrl = img.attr("src");
            if (!imgUrl.isEmpty()) {
                PostPhoto postPhoto = new PostPhoto(imgUrl, "ACTIVE");
                postPhoto.setPost(post);
                newPhotoList.add(postPhoto);
                postPhotoApiRepository.save(postPhoto);
            }
        }
        post.setPostPhotoList(newPhotoList);
        post.setPostTitle(title);
        post.setPostCategory(postCategory);
        post.setPostBody(content);
        postApiRepository.save(post);


        return ResponseEntity.ok("글이 성공적으로 수정되었습니다.");
    }

//    // 이미지 업로드 (게시글 작성 중 미리보기)
//    @PostMapping("/upload/image")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
//    @Operation(summary = "이미지 업로드", description = "이미지를 업로드하여 URL을 반환합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "upload success", content = @Content),
//            @ApiResponse(responseCode = "400", description = "bad request", content = @Content),
//            @ApiResponse(responseCode = "500", description = "upload failure", content = @Content)
//    })
//    public ResponseEntity<?> imageUpload(Principal principal, @RequestParam("image") MultipartFile imageFile) throws IOException {
//        if (imageFile.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("rs_st", -1, "rs_msg", "파일이 없습니다."));
//        }
//
//        try {
//            String imageUrl = storageService.storeImage(imageFile);
//            String thumbnailUrl = imageUrl; // 실제로는 썸네일 URL을 생성하거나 처리해야 할 수 있습니다.
//
//            Map<String, Object> fileInfo = new HashMap<>();
//            fileInfo.put("thumbnailPath", thumbnailUrl);
//            fileInfo.put("fullPath", imageUrl);
//            fileInfo.put("orgFilename", imageFile.getOriginalFilename());
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("rs_st", 0); // 성공 상태 코드
//            response.put("rs_data", fileInfo);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(Map.of("rs_st", -1, "rs_msg", "이미지 업로드 실패"));
//        }
//    }

    // 댓글 입력창 포커스시 로그인 상태 확인
    @GetMapping("/login/comment-write")
    @Operation(summary = "댓글 작성 로그인 확인 (미구현)", description = "댓글 입력창 포커스시 로그인 상태를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 확인 성공", content = @Content)
    })
    public ResponseEntity<String> commentWriteLogin(@JwtToken Integer userId) {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
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
}
