package com.kustaurant.restauranttier.tab4_community.controller;

import com.kustaurant.restauranttier.common.apiUser.JwtToken;
import com.kustaurant.restauranttier.tab4_community.dto.PostDTO;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.entity.PostPhoto;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
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
import com.kustaurant.restauranttier.tab5_mypage.service.MypageApiService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final MypageApiService mypageApiService;
    //    User userApiService.findUserById(24); = customOAuth2UserService.getUser(principal.getName());
    // 커뮤니티 메인 화면
    @GetMapping
    @Operation(summary = "커뮤니티 메인화면의 글 리스트 불러오기", description = "게시판 종류와 정렬 방법을 입력받고 해당 조건에 맞는 게시글 리스트가 반환됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "community request success", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PostDTO.class)))}),
            @ApiResponse(responseCode = "404", description = "community error", content = {@Content(mediaType = "application/json")})
    })
    public ResponseEntity<List<PostDTO>> community(
            @RequestParam(defaultValue = "전체") String postCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "recent") String sort) {
        Page<PostDTO> paging;
        if (postCategory.equals("전체")) {
            paging = postApiService.getList(page, sort);
        } else {
            paging = postApiService.getListByPostCategory(postCategory, page, sort);
        }

        return ResponseEntity.ok(paging.getContent());
    }

    // 커뮤니티 게시글 상세 화면
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 정보", description = "게시글 ID를 입력받고 해당 게시글의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "request success", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PostDTO.class))}),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<PostDTO> post(@PathVariable Integer postId, Principal principal, @RequestParam(defaultValue = "recent") String sort) {
        Post post = postApiService.getPost(postId);
        postApiService.increaseVisitCount(post);
        PostDTO postDTO = PostDTO.fromEntity(post);
        return ResponseEntity.ok(postDTO);
    }

    // 게시물 삭제
    @DeleteMapping("/{postId}")
    @Transactional
    @Operation(summary = "게시글 삭제", description = "게시글 ID를 입력받고 해당 게시글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<String> postDelete(@PathVariable Integer postId) {
        Post post = postApiService.getPost(Integer.valueOf(postId));

        //게시글 지워지면 그 게시글의 댓글들도 DELETED 상태로 변경
        List<PostComment> comments = post.getPostCommentList();
        for (PostComment comment : comments) {
            comment.setStatus("DELETED");
            //대댓글 삭제
            for (PostComment reply : comment.getRepliesList()) {
                reply.setStatus("DELETED");
            }
        }
        //게시글 지워지면 그 게시글의 scrab정보들도 다 지워야함
        List<PostScrap> scraps = post.getPostScrapList();
        postScrapApiRepository.deleteAll(scraps);
        // 사진 삭제
        List<PostPhoto> existingPhotos = post.getPostPhotoList();
        if (existingPhotos != null) {
            postPhotoApiRepository.deleteAll(existingPhotos);
            post.setPostPhotoList(null); // 기존 리스트 연결 해제
        }
        // 글 삭제
        post.setStatus("DELETED");
        return ResponseEntity.ok("post delete complete");
    }

    // 댓글, 대댓글 삭제
    @DeleteMapping("/comment/{commentId}")
    @Transactional
    @Operation(summary = "댓글 삭제", description = "댓글 ID를 입력받고 해당 댓글 및 대댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete success", content = @Content),
            @ApiResponse(responseCode = "404", description = "comment not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> commentDelete(@PathVariable Integer commentId) {
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentId);
        postComment.setStatus("DELETED");
        List<PostComment> repliesList = postComment.getRepliesList();
        int deletedCount = 1;
        // 해당 댓글에 대한 대댓글이 존재하면 삭제
        if (!repliesList.isEmpty()) {
            for (PostComment reply : repliesList) {
                if (!reply.getStatus().equals("DELETED")) {
                    reply.setStatus("DELETED");
                    deletedCount += 1;
                }

            }

        }
        // 응답에 삭제된 개수 포함
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deletedCount", deletedCount);
        response.put("message", "comment delete complete");

        return ResponseEntity.ok(response);
    }
    // 댓글 or 대댓글 생성
    @PostMapping("/comments")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "댓글 생성, 대댓글 생성", description = "게시글 ID와 내용을 입력받아 댓글을 생성합니다. 대댓글의 경우 부모 댓글의 id를 파라미터로 받아야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<String> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "parentCommentId", defaultValue = "") String parentCommentId,
            Model model, Principal principal) {
        Integer postIdInt = Integer.valueOf(postId);
        User user = mypageApiService.findUserById(24);;
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
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    // 게시글 좋아요 생성
    @PostMapping("/{postId}/likes")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 좋아요", description = "게시글 ID를 입력받아 좋아요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "like success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> postLikeCreate(@PathVariable String postId, Model model, @JwtToken Integer userId) {
        Integer postidInt = Integer.valueOf(postId);
        User user = mypageApiService.findUserById(userId);
        Post post = postApiService.getPost(postidInt);
        Map<String, Object> response = postApiService.likeCreateOrDelete(post, user);
        response.put("likeCount", post.getLikeUserList().size());
        response.put("dislikeCount", post.getDislikeUserList().size());

        return ResponseEntity.ok(response);
    }

    // 게시글 싫어요 생성
    @PostMapping("/{postId}/dislikes")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 싫어요", description = "게시글 ID를 입력받아 싫어요를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dislike success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> postDislikeCreate(@PathVariable String postId, Model model, Principal principal) {
        Integer postidInt = Integer.valueOf(postId);
        User user = mypageApiService.findUserById(24);;
        Post post = postApiService.getPost(postidInt);
        Map<String, Object> response = postApiService.dislikeCreateOrDelete(post, user);
        response.put("dislikeCount", post.getDislikeUserList().size());
        response.put("likeCount", post.getLikeUserList().size());
        return ResponseEntity.ok(response);
    }

    // 게시글 스크랩 (구현완료)
    @PostMapping("/{postId}/scraps")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 스크랩 ", description = "게시글 ID를 입력받아 스크랩을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "scrap success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> postScrap(@PathVariable String postId, Model model, Principal principal) {
        Integer postidInt = Integer.valueOf(postId);
        User user = mypageApiService.findUserById(24);;
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
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable String commentId, Principal principal) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentIdInt);
        User user = mypageApiService.findUserById(24);;
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
    public ResponseEntity<Map<String, Object>> dislikeComment(@PathVariable String commentId, Principal principal) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postApiCommentService.getPostCommentByCommentId(commentIdInt);
        User user = mypageApiService.findUserById(24);;
        Map<String, Object> response = postApiCommentService.dislikeCreateOrDelete(postComment, user);
        response.put("totalLikeCount", postComment.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 게시글 생성
    @PostMapping("/posts")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 생성", description = "게시글 제목,카테고리,내용,이미지를 입력받아 게시글을 생성합니다. 이미지 저장은 아직 미구현 상태입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create success", content = @Content),
            @ApiResponse(responseCode = "404", description = "user not found", content = @Content)
    })
    public ResponseEntity<String> postCreate(
            @RequestParam("title") String title,
            @RequestParam("postCategory") String postCategory,
            @RequestParam("content") String content,
            @RequestParam(value= "imgUrl" ,required = false) String imgUrl,
            Principal principal) throws IOException {

        // 게시글 객체 생성
        Post post = new Post(title, content, postCategory, "ACTIVE", LocalDateTime.now());
        User user = mypageApiService.findUserById(24);;
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

        return ResponseEntity.ok("글이 성공적으로 저장되었습니다.");
    }

    // 게시글 수정
    @PatchMapping("/posts/{postId}")
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "게시글 수정", description = "게시글 ID와 내용을 입력받아 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update success", content = @Content),
            @ApiResponse(responseCode = "404", description = "post not found", content = @Content)
    })
    public ResponseEntity<String> postUpdate(
            @PathVariable String postId,
            @RequestParam String title,
            @RequestParam String postCategory,
            @RequestParam String content
    ) {


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

//        List<PostPhoto> newPhotoList = new ArrayList<>();
//        // TinyMCE 컨텐츠에서 <img> 태그를 파싱
//        Document doc = Jsoup.parse(content);
//        Elements imgTags = doc.select("img");
//
//        // 각 <img> 태그에 대해 이미지 생성하고 post에 추가
//        for (Element img : imgTags) {
//            String imgUrl = img.attr("src");
//
//            // 이미지 파일 처리
//            if (!imgUrl.isEmpty()) {
//                PostPhoto postPhoto = new PostPhoto(imgUrl, "ACTIVE");
//                postPhoto.setPost(post); // 게시글과 이미지 연관관계 설정
//                newPhotoList.add(postPhoto); // post의 이미지 리스트에 추가
//                postPhotoRepository.save(postPhoto); // 이미지 정보 저장
//            }
//        }
//
//        post.setPostPhotoList(newPhotoList);
//        post.setPostTitle(title);
//        post.setPostCategory(postCategory);
//        post.setPostBody(content);
//
//
//        postRepository.save(post);


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
    @GetMapping("/api/v1/login/comment-write")
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @Operation(summary = "댓글 작성 로그인 확인", description = "댓글 입력창 포커스시 로그인 상태를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 확인 성공", content = @Content)
    })
    public ResponseEntity<String> commentWriteLogin() {
        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
    }
}
