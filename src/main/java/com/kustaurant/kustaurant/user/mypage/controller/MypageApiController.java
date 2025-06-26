package com.kustaurant.kustaurant.user.mypage.controller;

import com.kustaurant.kustaurant.feedback.controller.Request.FeedbackRequest;
import com.kustaurant.kustaurant.feedback.controller.port.FeedbackService;
import com.kustaurant.kustaurant.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.*;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MypageMainResponse;
import com.kustaurant.kustaurant.user.mypage.service.MypageApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MypageApiController {

    private final MypageApiService mypageApiService;
    private final FeedbackService feedbackService;

    //1-1
    @Operation(
            summary = "\"마이페이지 화면\" 로드에 필요한 정보 불러오기(/auth 없음)",
            description = "마이페이지 화면에 필요한 정보들이 반환됩니다. " +
                    "로그인하지 않은 회원도 접속 가능하기 때문에 엔드포인트에 /auth가 포함되지 않습니다. " +
                    "로그인하지 않은 회원인 경우 기본적인 빈 객체가 반환됩니다."
    )
    @GetMapping("/mypage")
    public ResponseEntity<MypageMainResponse> getMypageView(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        MypageMainResponse response;

        if (user == null) {
            // 로그인하지 않은 사용자일 경우, 빈 객체 반환
            response = new MypageMainResponse(null,null,0,0);
        } else {
            // 로그인한 사용자의 마이페이지 정보 로드
            response = mypageApiService.getMypageInfo(user.id());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    //2
    @Operation(
            summary = "마이페이지 프로필 정보(변경)화면 로드에 정보 불러오기",
            description = "마이페이지 프로필 정보(변경)화면 로드에 정보를 불러옵니다"
    )
    @GetMapping("/auth/mypage/profile")
    public ResponseEntity<ProfileResponse> getMypageProfile(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        ProfileResponse profileResponse = mypageApiService.getProfile(user.id());

        return new ResponseEntity<>(profileResponse, HttpStatus.OK);
    }


    //3
    @Operation(
            summary = "마이페이지 프로필 변경하기",
            description = "유저의 닉네임,전화번호를 변경합니다. (프로필 사진 미구현)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "변경된 사항이 없습니다 or" +
                    " 닉네임을 변경한 지 30일이 지나지 않아 변경할 수 없습니다 or" +
                    "해당 닉네임이 이미 존재합니다. or" +
                    "이전과 동일한 닉네임입니다. or" +
                    "닉네임은 2자 이상이어야 합니다. or" +
                    "닉네임은 10자 이하여야 합니다. or" +
                    "전화번호는 숫자로 11자로만 입력되어야 합니다.")
    })
    @PatchMapping("/auth/mypage/profile")
    public ResponseEntity<?> updateMypageProfile(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Valid @RequestBody ProfileUpdateRequest req
    ){
        ProfileResponse res = mypageApiService.updateUserProfile(user.id(), req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    //4
    @Operation(
            summary = "\"내가 평가한 맛집 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 평가해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/evaluate-restaurant-list")
    public ResponseEntity<List<MyRatedRestaurantResponse>> getEvaluateRestaurantList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyRatedRestaurantResponse> list = mypageApiService.getUserEvaluateRestaurantList(user.id());

        return ResponseEntity.ok(list);
    }


    //5 커뮤니티
    @Operation(
            summary = "\"내가 작성한 커뮤니티글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티 글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-list")
    public ResponseEntity<List<MyPostsResponse>> getWrittenUserPostsList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> list = mypageApiService.getUserPosts(user.id());

        return ResponseEntity.ok(list);
    }


    //6
    @Operation(
            summary = "\"내가 저장한 맛집 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 즐겨찾기해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/favorite-restaurant-list")
    public ResponseEntity<List<MyRestaurantResponse>> getFavoriteRestaurantList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyRestaurantResponse> list = mypageApiService.getUserFavoriteRestaurantList(user.id());

        return ResponseEntity.ok(list);
    }


    //7 커뮤니티
    @Operation(
            summary = "\"내가 저장한 커뮤니티 게시글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 저장해놓은 커뮤니티 게시글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-scrap-list")
    public ResponseEntity<List<MyPostsResponse>> getCommunityScrapList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> list = mypageApiService.getScrappedUserPosts(user.id());

        return ResponseEntity.ok(list);
    }


    //8 커뮤니티
    @Operation(
            summary = "\"내가 작성한 커뮤니티 댓글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티의 댓글 리스트들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-comment-list")
    public ResponseEntity<List<MyPostCommentResponse>> getCommunityCommentList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostCommentResponse> list = mypageApiService.getCommentedUserPosts(user.id());

        return ResponseEntity.ok(list);
    }


    //9
    @Operation(
            summary = "피드백 보내기 기능입니다",
            description = "피드백을 보냅니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "피드백 감사합니다!"),
            @ApiResponse(responseCode = "400", description = "내용이 없습니다."),
    })
    @PostMapping("/auth/mypage/feedback")
    public ResponseEntity<String> sendFeedback(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Valid @RequestBody FeedbackRequest req
    ){
        feedbackService.create(user.id(),req);

        return ResponseEntity.status(HttpStatus.OK).body("피드백 감사합니다!");
    }


    //10
    @Operation(
            summary = "\"공지사항 목록화면\" 로드에 필요한 정보 불러오기",
            description = "공지사항 리스트와 관련 링크들을 불러옵니다."
    )
    @GetMapping("/mypage/noticelist")
    public ResponseEntity<List<NoticeDTO>> getNotices() {
        List<NoticeDTO> noticeDTOs = mypageApiService.getAllNotices();

        return ResponseEntity.ok(noticeDTOs);
    }
}
