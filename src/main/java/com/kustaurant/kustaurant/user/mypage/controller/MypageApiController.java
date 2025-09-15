package com.kustaurant.kustaurant.user.mypage.controller;

import com.kustaurant.kustaurant.admin.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MypageApiController {
    private final MypageService mypageApiService;

    //1-1
    @Operation(
            summary = "\"마이페이지 화면\" 로드에 필요한 정보 불러오기",
            description = "마이페이지 화면에 필요한 정보들이 반환됩니다. " +
                    "로그인하지 않은 회원도 접속 가능하기 때문에 엔드포인트에 /auth가 포함되지 않습니다. " +
                    "로그인하지 않은 회원인 경우 기본적인 빈 객체가 반환됩니다." +
                    "웹과 response객체를 같이 쓰다 보니 앱에서 사용하지 않는 값이 있습니다. 그 값들은 무시하시면 됩니다."
    )
    @GetMapping("/v2/mypage")
    public ResponseEntity<ProfileResponse> getMypageView(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        ProfileResponse response;
        if (user == null) {
            response = new ProfileResponse(null,0,0,0,0,0,null,null);
        } else{
            response = mypageApiService.getProfile(user.id());
        }

        return ResponseEntity.ok(response);
    }


    //2 마이페이지 프로필 변경 화면 조회
    @Operation(
            summary = "\"마이페이지 프로필 변경 화면 조회\"",
            description = "마이페이지 프로필 변경화면 로드에 정보를 불러옵니다"
    )
    @GetMapping("/v2/mypage/profile")
    public ResponseEntity<ProfileResponse> getMypageProfile(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        ProfileResponse response = mypageApiService.getProfile(user.id());

        return ResponseEntity.ok(response);
    }


    //3 마이페이지 프로필 변경
    @Operation(
            summary = "마이페이지 프로필 변경하기",
            description = "유저의 닉네임,전화번호를 변경합니다. (프로필 사진 미구현)" +
                    "닉네임 또는 전화번호 중 하나만 변경요청 가능하므로 "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "요청에 변경된 값이 없습니다 or" +
                    "닉네임 변경은 30일에 한 번만 가능합니다. or" +
                    "이미 사용 중인 닉네임 입니다. or" +
                    "닉네임은 2자 이상 10지 이하여야 합니다. or" +
                    "이미 사용 중인 전화번호 입니다. or" +
                    "전화번호는 숫자로만 11자리여야 합니다.('-'제외)")
    })
    @PatchMapping("/v2/auth/mypage/profile")
    public ResponseEntity<ProfileUpdateResponse> updateMypageProfile(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user,
            @Valid @RequestBody ProfileUpdateRequest req
    ){
        ProfileUpdateResponse response = mypageApiService.updateUserProfile(user.id(), req);

        return ResponseEntity.ok(response);
    }


    //4 음식점 평가 목록 조회
    @Operation(
            summary = "\"내가 평가한 음식점 조회\"",
            description = "유저가 평가한 음식점 리스트를 불러옵니다."
    )
    @GetMapping("/v2/auth/mypage/restaurants/evaluated")
    public ResponseEntity<List<MyRatedRestaurantResponse>> getEvaluateRestaurantList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyRatedRestaurantResponse> responseList = mypageApiService.getUserEvaluateRestaurantList(user.id());

        return ResponseEntity.ok(responseList);
    }


    //5 음식점 즐겨찾기 조회
    @Operation(
            summary = "\"내가 저장한 음식점 조회\"",
            description = "유저가 즐겨찾기해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/v2/auth/mypage/restaurants/favorite")
    public ResponseEntity<List<MyRestaurantResponse>> getFavoriteRestaurantList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyRestaurantResponse> responseList = mypageApiService.getUserFavoriteRestaurantList(user.id());

        return ResponseEntity.ok(responseList);
    }


    //6 커뮤니티 작성 글 조회
    @Operation(
            summary = "\"내가 작성한 커뮤니티 글 조회\"",
            description = "유저가 작성한 커뮤니티 글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/v2/auth/mypage/community/posts")
    public ResponseEntity<List<MyPostsResponse>> getWrittenUserPostsList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> responseList = mypageApiService.getUserPosts(user.id());

        return ResponseEntity.ok(responseList);
    }


    //7 커뮤니티 스크랩한 게시글 조회
    @Operation(
            summary = "\"내가 저장한 커뮤니티 게시글 조회\"",
            description = "유저가 저장해놓은 커뮤니티 게시글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/v2/auth/mypage/community/scraps")
    public ResponseEntity<List<MyPostsResponse>> getCommunityScrapList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostsResponse> list = mypageApiService.getScrappedUserPosts(user.id());

        return ResponseEntity.ok(list);
    }


    //8 커뮤니티 작성 댓글 조회
    @Operation(
            summary = "\"내가 작성한 커뮤니티 댓글 조회\"",
            description = "유저가 작성한 커뮤니티의 댓글 리스트들을 불러옵니다."
    )
    @GetMapping("/v2/auth/mypage/community/comments")
    public ResponseEntity<List<MyPostCommentResponse>> getCommunityCommentList(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ){
        List<MyPostCommentResponse> list = mypageApiService.getCommentedUserPosts(user.id());

        return ResponseEntity.ok(list);
    }


    //9 공지사항
    @Operation(
            summary = "\"공지사항 목록 조회\"",
            description = "공지사항 리스트와 관련 링크들을 불러옵니다. (조회는 웹뷰 방식)"
    )
    @GetMapping("/v2/mypage/notices")
    public ResponseEntity<List<NoticeDTO>> getNotices() {
        List<NoticeDTO> noticeDTOs = mypageApiService.getAllNotices();

        return ResponseEntity.ok(noticeDTOs);
    }
}
