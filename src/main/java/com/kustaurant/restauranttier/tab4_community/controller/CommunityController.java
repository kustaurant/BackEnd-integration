package com.kustaurant.restauranttier.tab4_community.controller;

import com.kustaurant.restauranttier.tab1_home.controller.MainController;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.entity.PostPhoto;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab4_community.service.PostCommentService;
import com.kustaurant.restauranttier.tab4_community.service.PostScrapService;
import com.kustaurant.restauranttier.tab4_community.service.PostService;
import com.kustaurant.restauranttier.tab4_community.service.StorageService;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab4_community.repository.PostCommentRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostScrapRepository;
import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
import com.kustaurant.restauranttier.tab4_community.repository.PostPhotoRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class CommunityController {
    private final PostService postService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final PostCommentService postCommentService;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostScrapService postScrapService;
    private final PostPhotoRepository postPhotoRepository;
    private final StorageService storageService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    // 커뮤니티 메인 화면
    @GetMapping("/community")
    public String community(
            Model model,
            @RequestParam(defaultValue = "전체") String postCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(defaultValue = "recent") String sort) {
        Page<Post> paging;
        // 따로 전송된 카테고리 값이 없을떄
        if (postCategory.equals("전체")) {
            paging = postService.getList(page, sort);
        }
        // 카테고리 값이 있을 때
        else {
            paging = postService.getListByPostCategory(postCategory, page, sort);
        }
        model.addAttribute("currentPage", "community");
        model.addAttribute("postCategory", postCategory);
        model.addAttribute("sort", sort);
        model.addAttribute("paging", paging);
        return "community";
    }

    // 커뮤니티 게시글 상세 화면
    @GetMapping("/community/{postId}")
    public String post(Model model, @PathVariable Integer postId, Principal principal, @RequestParam(defaultValue = "recent") String sort) {
        Post post = postService.getPost(postId);
        // 조회수 증가
        postService.increaseVisitCount(post);
        List<PostComment> postCommentList = postCommentService.getList(postId, sort);
        model.addAttribute("postCommentList", postCommentList);
        model.addAttribute("post", post);
        boolean isPostScrappedByUser = false;
        if (principal != null) {
            User user = customOAuth2UserService.getUser(principal.getName());
            model.addAttribute("user", user);
            isPostScrappedByUser = post.getPostScrapList().stream()
                    .anyMatch(scrap -> scrap.getUser().equals(user));
        }
        ;
        model.addAttribute("sort", sort);
        model.addAttribute("isPostScrappedByUser", isPostScrappedByUser);
        return "community_post";
    }

    // 게시물 삭제
    @GetMapping("/api/post/delete")
    @Transactional
    public ResponseEntity<String> postDelete(@RequestParam String postId) {
        Post post = postService.getPost(Integer.valueOf(postId));

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
        postScrapRepository.deleteAll(scraps);
        // 사진 삭제
        List<PostPhoto> existingPhotos = post.getPostPhotoList();
        if (existingPhotos != null) {
            postPhotoRepository.deleteAll(existingPhotos);
            post.setPostPhotoList(null); // 기존 리스트 연결 해제
        }
        // 글 삭제
        post.setStatus("DELETED");
        return ResponseEntity.ok("post delete complete");
    }

    // 댓글 ,대댓글 삭제
    @Transactional
    @GetMapping("/api/comment/delete")
    public ResponseEntity<Map<String, Object>> commentDelete(@RequestParam Integer commentId) {
        PostComment postComment = postCommentService.getPostCommentByCommentId(commentId);
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
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/community/comment/create")
    public ResponseEntity<String> postCommentCreate(
            @RequestParam(name = "content", defaultValue = "") String content,
            @RequestParam(name = "postId") String postId,
            @RequestParam(name = "parentCommentId", defaultValue = "") String parentCommentId,
            Model model, Principal principal) {
        Integer postIdInt = Integer.valueOf(postId);
        User user = customOAuth2UserService.getUser(principal.getName());
        Post post = postService.getPost(postIdInt);
        PostComment postComment = new PostComment(content, "ACTIVE", LocalDateTime.now(), post, user);
        PostComment savedPostComment = postCommentRepository.save(postComment);

        // 대댓글이면 부모 관계 매핑하기
        if (!parentCommentId.isEmpty()) {
            PostComment parentComment = postCommentService.getPostCommentByCommentId(Integer.valueOf(parentCommentId));
            savedPostComment.setParentComment(parentComment);
            parentComment.getRepliesList().add(savedPostComment);
            postCommentService.replyCreate(user, savedPostComment);
            postCommentRepository.save(parentComment);
        }
        // 댓글이면 post와 연결하면서 저장
        else {
            postCommentService.create(post, user, savedPostComment);
        }
        postCommentRepository.save(savedPostComment);
        return ResponseEntity.ok("댓글이 성공적으로 저장되었습니다.");
    }

    // 게시글 좋아요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/like")
    public ResponseEntity<Map<String, Object>> postLikeCreate(@RequestParam("postId") String postId, Model model, Principal principal) {
        Integer postidInt = Integer.valueOf(postId);
        User user = customOAuth2UserService.getUser(principal.getName());
        Post post = postService.getPost(postidInt);
        Map<String, Object> response = postService.likeCreateOrDelete(post, user);
        response.put("likeCount", post.getLikeUserList().size());
        response.put("dislikeCount", post.getDislikeUserList().size());

        return ResponseEntity.ok(response);
    }

    // 게시글 싫어요 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/dislike")
    public ResponseEntity<Map<String, Object>> postDislikeCreate(@RequestParam("postId") String postId, Model model, Principal principal) {
        Integer postidInt = Integer.valueOf(postId);
        User user = customOAuth2UserService.getUser(principal.getName());
        Post post = postService.getPost(postidInt);
        Map<String, Object> response = postService.dislikeCreateOrDelete(post, user);
        response.put("dislikeCount", post.getDislikeUserList().size());
        response.put("likeCount", post.getLikeUserList().size());
        return ResponseEntity.ok(response);
    }

    // 게시글 스크랩
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/post/scrap")
    public ResponseEntity<Map<String, Object>> postScrap(@RequestParam("postId") String postId, Model model, Principal principal) {
        Integer postidInt = Integer.valueOf(postId);
        User user = customOAuth2UserService.getUser(principal.getName());
        Post post = postService.getPost(postidInt);
        Map<String, Object> response = postScrapService.scrapCreateOfDelete(post, user);
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
    public ResponseEntity<Map<String, Object>> likeComment(@PathVariable String commentId, Principal principal) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postCommentService.getPostCommentByCommentId(commentIdInt);
        User user = customOAuth2UserService.getUser(principal.getName());
        Map<String, Object> response = postCommentService.likeCreateOrDelete(postComment, user);
        response.put("totalLikeCount", postComment.getLikeCount());
        return ResponseEntity.ok(response);
    }

    // 게시글 댓글 싫어요
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/comment/dislike/{commentId}")
    public ResponseEntity<Map<String, Object>> dislikeComment(@PathVariable String commentId, Principal principal) {
        Integer commentIdInt = Integer.valueOf(commentId);
        PostComment postComment = postCommentService.getPostCommentByCommentId(commentIdInt);
        User user = customOAuth2UserService.getUser(principal.getName());
        Map<String, Object> response = postCommentService.dislikeCreateOrDelete(postComment, user);
        response.put("totalLikeCount", postComment.getLikeCount());
        return ResponseEntity.ok(response);
    }


// 게시글 작성 화면 관련

    // 커뮤니티 게시글 작성 화면
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/community/write")
    public String write() {
        return "community_write";
    }


    // 게시글 생성
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/community/post/create")
    public ResponseEntity<String> postCreate(
            @RequestParam("title") String title,
            @RequestParam("postCategory") String postCategory,
            @RequestParam("content") String content,
            Principal principal) throws IOException {

        // 게시글 객체 생성
        Post post = new Post(title, content, postCategory, "ACTIVE", LocalDateTime.now());
        User user = customOAuth2UserService.getUser(principal.getName());
        postService.create(post, user);

        // TinyMCE 컨텐츠에서 <img> 태그를 파싱
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");

        // 각 <img> 태그에 대해 이미지 생성하고 post에 추가
        for (Element img : imgTags) {
            String imgUrl = img.attr("src");
            // 여기서 이미지 URL의 유효성을 검사하고 필요한 처리를 수행하세요.

            // 이미지 파일 처리
            if (imgUrl != null && !imgUrl.isEmpty()) {
                PostPhoto postPhoto = new PostPhoto(imgUrl, "ACTIVE");
                postPhoto.setPost(post); // 게시글과 이미지 연관관계 설정
                post.getPostPhotoList().add(postPhoto); // post의 이미지 리스트에 추가
                postPhotoRepository.save(postPhoto); // 이미지 정보 저장
            }
        }

        // 게시글 정보 저장
        postRepository.save(post);

        return ResponseEntity.ok("글이 성공적으로 저장되었습니다.");
    }

    //게시글 수정화면
    @GetMapping("/community/post/update")
    public String postUpdatePage(@RequestParam String postId, Model model) {
        logger.info(postId);
        Post post = postService.getPost(Integer.valueOf(postId));
        model.addAttribute("post", post);
        logger.info(post.toString());

        return "community_update";
    }

    // 게시글 수정
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/community/post/update")
    public ResponseEntity<String> postUpdate(
            @RequestParam String postId,
            @RequestParam String title,
            @RequestParam String postCategory,
            @RequestParam String content
    ) {


        Post post = postService.getPost(Integer.valueOf(postId));
        // 기존 연관된 사진 정보 삭제
        List<PostPhoto> existingPhotos = post.getPostPhotoList();
        if (existingPhotos != null) {
            postPhotoRepository.deleteAll(existingPhotos);
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
                postPhotoRepository.save(postPhoto);
            }
        }
        post.setPostPhotoList(newPhotoList);
        post.setPostTitle(title);
        post.setPostCategory(postCategory);
        post.setPostBody(content);
        postRepository.save(post);

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

    // 이미지 업로드 (미리보기)
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PostMapping("/api/upload/image")
    public ResponseEntity<?> imageUpload(Principal principal, @RequestParam("image") MultipartFile imageFile) throws IOException {
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


//    // 댓글 입력창 포커스시 로그인 상태 확인
//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
//    @GetMapping("/api/login/comment-write")
//    public ResponseEntity<String> commentWriteLogin() {
//        return ResponseEntity.ok("로그인이 성공적으로 되어있습니다.");
//
//    }


}
