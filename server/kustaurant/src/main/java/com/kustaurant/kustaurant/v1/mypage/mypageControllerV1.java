package com.kustaurant.kustaurant.v1.mypage;

import com.kustaurant.kustaurant.admin.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.v1.mypage.dto.*;
import com.kustaurant.kustaurant.admin.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.admin.notice.domain.Notice;
import com.kustaurant.kustaurant.common.util.UserIconResolver;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;
import com.kustaurant.kustaurant.v1.mypage.dto.*;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Hidden
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
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @AuthUser AuthUserInfo user
    ){
        ProfileResponse v2;
        MypageMainDTO v1;
        if (user.id() == null) {
            v1 = new MypageMainDTO();
        } else{
            v2 = mypageService.getProfile(user.id());
            v1 = new MypageMainDTO(UserIconResolver.resolve(v2.evalCnt()), v2.nickname(), v2.evalCnt(), v2.postCnt());
        }

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //1-2 프로필 조회
    @GetMapping("/auth/mypage")
    public ResponseEntity<MypageMainDTO> getMypageView2(
            @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @AuthUser AuthUserInfo user
    ){
        ProfileResponse v2;
        MypageMainDTO v1;
        if (user.id() == null) {
            v1 = new MypageMainDTO();
        } else{
            v2 = mypageService.getProfile(user.id());
            v1 = new MypageMainDTO(UserIconResolver.resolve(v2.evalCnt()), v2.nickname(), v2.evalCnt(), v2.postCnt());
        }

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //2 프로필 조회
    @GetMapping("/auth/mypage/profile")
    public ResponseEntity<ProfileDTO> getMypageProfile(
            @AuthUser AuthUserInfo user
    ){
        ProfileResponse v2 = mypageService.getProfile(user.id());
        ProfileDTO v1 = new ProfileDTO(v2.nickname(), v2.email(), v2.phoneNumber());

        return new ResponseEntity<>(v1, HttpStatus.OK);
    }


    //3 프로필 변경
    @PatchMapping("/auth/mypage/profile")
    public ResponseEntity<?> updateMypageProfile(
            @AuthUser AuthUserInfo user,
            @RequestBody ProfileDTO request
    ){
        ProfileUpdateRequest v2 = new ProfileUpdateRequest(request.getNickname(), request.getEmail());

        try {
            ProfileUpdateResponse v1 = mypageService.updateUserProfile(user.id(), v2);
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
            @AuthUser AuthUserInfo user
    ){
        List<MyRatedRestaurantResponse> v2 = mypageService.getUserEvaluateRestaurantList(user.id());

        return ResponseEntity.ok(mapper.toLegacyRatedList(v2));
    }


    //5 커뮤니티 작성 글
    @GetMapping("/auth/mypage/community-list")
    public ResponseEntity<List<MypagePostDTO>> getWrittenUserPostsList(
            @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> v2 = mypageService.getUserPosts(user.id());

        return ResponseEntity.ok(mapper.toLegacyPostList(v2));
    }


    //6 즐겨찾기 식당
    @GetMapping("/auth/mypage/favorite-restaurant-list")
    public ResponseEntity<List<FavoriteRestaurantInfoDTO>> getFavoriteRestaurantList(
            @AuthUser AuthUserInfo user
    ){
        List<MyRestaurantResponse> v2 = mypageService.getUserFavoriteRestaurantList(user.id());

        return ResponseEntity.ok(mapper.toLegacyFavList(v2));
    }


    //7 커뮤니티 스크랩
    @GetMapping("/auth/mypage/community-scrap-list")
    public ResponseEntity<List<MypagePostDTO>> getCommunityScrapList(
            @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> v2 = mypageService.getScrappedUserPosts(user.id());
        return ResponseEntity.ok(mapper.toLegacyPostList(v2));
    }


    //8 커뮤니티 댓글 목록
    @GetMapping("/auth/mypage/community-comment-list")
    public ResponseEntity<List<MypagePostCommentDTO>> getCommunityCommentList(
            @AuthUser AuthUserInfo user
    ){
        List<MyPostCommentResponse> v2 = mypageService.getCommentedUserPosts(user.id());
        return ResponseEntity.ok(mapper.toLegacyCommentList(v2));
    }


    //9 피드백 보내기
    @PostMapping("/auth/mypage/feedback")
    public ResponseEntity<MypageErrorDTO> sendFeedback(
            @AuthUser AuthUserInfo user,
            @RequestBody FeedbackDTO request
    ){
        String comments = request.getComments();
        MypageErrorDTO response = new MypageErrorDTO();

        if (comments == null) {
            response.setError("내용이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        FeedbackRequest req = new FeedbackRequest(request.getComments());
        feedbackService.create(user.id(), req);

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
