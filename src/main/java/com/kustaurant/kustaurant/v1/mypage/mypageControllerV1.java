package com.kustaurant.kustaurant.v1.mypage;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.admin.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.admin.notice.domain.Notice;
import com.kustaurant.kustaurant.common.util.UserIconResolver;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;
import com.kustaurant.kustaurant.v1.common.JwtToken;
import com.kustaurant.kustaurant.v1.mypage.dto.*;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class mypageControllerV1 {

    private final MypageService mypageService;
    private final FeedbackService feedbackService;
    private final MypageCompatMapper mapper;

    //1-1
    @GetMapping("/mypage")
    public ResponseEntity<MypageMainDTO> getMypageView(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        ProfileResponse v2;
        MypageMainDTO v1;
        if (userId == null) {
            v1 = new MypageMainDTO();
        } else{
            v2 = mypageService.getProfile(userId);
            v1 = new MypageMainDTO(UserIconResolver.resolve(v2.evalCnt()), v2.nickname(), v2.evalCnt(), v2.postCnt());
        }

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //1-2 프로필 조회
    @GetMapping("/auth/mypage")
    public ResponseEntity<MypageMainDTO> getMypageView2(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        ProfileResponse v2;
        MypageMainDTO v1;
        if (userId == null) {
            v1 = new MypageMainDTO();
        } else{
            v2 = mypageService.getProfile(userId);
            v1 = new MypageMainDTO(UserIconResolver.resolve(v2.evalCnt()), v2.nickname(), v2.evalCnt(), v2.postCnt());
        }

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //2 프로필 조회
    @GetMapping("/auth/mypage/profile")
    public ResponseEntity<ProfileDTO> getMypageProfile(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        ProfileResponse v2 = mypageService.getProfile(userId);
        ProfileDTO v1 = new ProfileDTO(v2.nickname(), v2.email(), v2.phoneNumber());

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //3 프로필 변경
    @PatchMapping("/auth/mypage/profile")
    public ResponseEntity<?> updateMypageProfile(
            @Parameter(hidden = true) @JwtToken Long userId,
            @RequestBody ProfileDTO request
    ){
        ProfileUpdateRequest v2 = new ProfileUpdateRequest(request.getNickname(), request.getEmail());

        try {
            ProfileUpdateResponse v1 = mypageService.updateUserProfile(userId, v2);
            return new ResponseEntity<>(v1, HttpStatus.OK);
        } catch (Exception e) {
            MypageErrorDTO profileError = new MypageErrorDTO();
            profileError.setError(e.getMessage());
            return new ResponseEntity<>(profileError, HttpStatus.BAD_REQUEST);
        }
    }


    //4 평가한 식당 목록
    @GetMapping("/auth/mypage/evaluate-restaurant-list")
    public ResponseEntity<List<EvaluatedRestaurantInfoDTO>> getEvaluateRestaurantList(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        List<MyRatedRestaurantResponse> v2 = mypageService.getUserEvaluateRestaurantList(userId);

        return ResponseEntity.ok(mapper.toLegacyRatedList(v2));
    }


    //5 커뮤니티 작성 글
    @GetMapping("/auth/mypage/community-list")
    public ResponseEntity<List<MypagePostDTO>> getWrittenUserPostsList(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        List<MyPostsResponse> v2 = mypageService.getUserPosts(userId);

        return ResponseEntity.ok(mapper.toLegacyPostList(v2));
    }


    //6 즐겨찾기 식당
    @GetMapping("/auth/mypage/favorite-restaurant-list")
    public ResponseEntity<List<FavoriteRestaurantInfoDTO>> getFavoriteRestaurantList(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        List<MyRestaurantResponse> v2 = mypageService.getUserFavoriteRestaurantList(userId);

        return ResponseEntity.ok(mapper.toLegacyFavList(v2));
    }


    //7 커뮤니티 스크랩
    @GetMapping("/auth/mypage/community-scrap-list")
    public ResponseEntity<List<MypagePostDTO>> getCommunityScrapList(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        List<MyPostsResponse> v2 = mypageService.getScrappedUserPosts(userId);
        return ResponseEntity.ok(mapper.toLegacyPostList(v2));
    }


    //8 커뮤니티 댓글 목록
    @GetMapping("/auth/mypage/community-comment-list")
    public ResponseEntity<List<MypagePostCommentDTO>> getCommunityCommentList(
            @Parameter(hidden = true) @JwtToken Long userId
    ){
        List<MyPostCommentResponse> v2 = mypageService.getCommentedUserPosts(userId);
        return ResponseEntity.ok(mapper.toLegacyCommentList(v2));
    }


    //9 피드백 보내기
    @PostMapping("/auth/mypage/feedback")
    public ResponseEntity<MypageErrorDTO> sendFeedback(
            @Parameter(hidden = true) @JwtToken Long userId,
            @RequestBody FeedbackDTO request
    ){
        String comments = request.getComments();
        MypageErrorDTO response = new MypageErrorDTO();

        if (comments == null) {
            response.setError("내용이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        FeedbackRequest req = new FeedbackRequest(request.getComments());
        feedbackService.create(userId, req);

        response.setError("피드백 감사합니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    //10
    @GetMapping("/mypage/noticelist")
    public ResponseEntity<List<NoticeDTO>> getNotices() {
        List<Notice> v2 = mypageService.getAllNotices();
        return ResponseEntity.ok(mapper.toLegacyNoticeList(v2));
    }
}
