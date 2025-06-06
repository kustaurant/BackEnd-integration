package com.kustaurant.kustaurant.common.user.controller.api;

import com.kustaurant.kustaurant.common.notice.domain.NoticeDTO;
import com.kustaurant.kustaurant.common.user.controller.api.response.*;
import com.kustaurant.kustaurant.global.auth.jwt.customAnno.JwtToken;
import com.kustaurant.kustaurant.common.user.service.MypageApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MypageApiController {

    private final MypageApiService mypageApiService;


    //1-1
    @Operation(
            summary = "\"마이페이지 화면\" 로드에 필요한 정보 불러오기(/auth 없음)",
            description = "마이페이지 화면에 필요한 정보들이 반환됩니다. " +
                    "로그인하지 않은 회원도 접속 가능하기 때문에 엔드포인트에 /auth가 포함되지 않습니다. " +
                    "로그인하지 않은 회원인 경우 기본적인 빈 객체가 반환됩니다."
    )
    @GetMapping("/mypage")
    public ResponseEntity<MypageMainDTO> getMypageView(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ){
        MypageMainDTO mypageMainDTO;

        if (userId == null) {
            // 로그인하지 않은 사용자일 경우, 빈 객체 반환
            mypageMainDTO = new MypageMainDTO(); // 기본 생성자를 통해 빈 객체 생성
        } else {
            // 로그인한 사용자의 마이페이지 정보 로드
            mypageMainDTO = mypageApiService.getMypageInfo(userId, userAgent);
        }

        return new ResponseEntity<>(mypageMainDTO, HttpStatus.OK);
    }


    //1-2
    @Operation(
            summary = "\"마이페이지 화면\" 로드에 필요한 정보 불러오기(/auth 있음)",
            description = "마이페이지 화면에 필요한 정보들이 반환됩니다. " +
                    "로그인하지 않은 회원도 접속 가능하기 때문에 엔드포인트에 /auth가 포함됩니다. " +
                    "로그인하지 않은 회원인 경우 기본적인 빈 객체가 반환됩니다."
    )
    @GetMapping("/auth/mypage")
    public ResponseEntity<MypageMainDTO> getMypageView2(
            @Parameter(hidden = true) @RequestHeader(value = HttpHeaders.USER_AGENT, required = false) String userAgent,
            @Parameter(hidden = true) @JwtToken Integer userId
    ){
        MypageMainDTO mypageMainDTO;

        if (userId == null) {
            // 로그인하지 않은 사용자일 경우, 빈 객체 반환
            mypageMainDTO = new MypageMainDTO(); // 기본 생성자를 통해 빈 객체 생성
        } else {
            // 로그인한 사용자의 마이페이지 정보 로드
            mypageMainDTO = mypageApiService.getMypageInfo(userId, userAgent);
        }

        return new ResponseEntity<>(mypageMainDTO, HttpStatus.OK);
    }


    //2
    @Operation(
            summary = "마이페이지 프로필 정보(변경)화면 로드에 정보 불러오기",
            description = "마이페이지 프로필 정보(변경)화면 로드에 정보를 불러옵니다"
    )
    @GetMapping("/auth/mypage/profile")
    public ResponseEntity<ProfileDTO> getMypageProfile(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        ProfileDTO profileDTO = mypageApiService.getProfileInfo(userId);
        return new ResponseEntity<>(profileDTO, HttpStatus.OK);
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
            @Parameter(hidden = true) @JwtToken Integer userId,
            @RequestBody ProfileDTO receivedProfileDTO
    ){
        try {
            ProfileDTO updatedProfile = mypageApiService.updateUserProfile(userId, receivedProfileDTO);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            MypageErrorDTO profileError = new MypageErrorDTO();
            profileError.setError(e.getMessage());
            return new ResponseEntity<>(profileError, HttpStatus.BAD_REQUEST);
        }
    }


    //4
    @Operation(
            summary = "\"내가 평가한 맛집 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 평가해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/evaluate-restaurant-list")
    public ResponseEntity<List<EvaluatedRestaurantInfoDTO>> getEvaluateRestaurantList(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        List<EvaluatedRestaurantInfoDTO> userEvaluateRestaurantList = mypageApiService.getUserEvaluateRestaurantList(userId);
        return new ResponseEntity<>(userEvaluateRestaurantList, HttpStatus.OK);
    }


    //5 커뮤니티
    @Operation(
            summary = "\"내가 작성한 커뮤니티글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티 글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-list")
    public ResponseEntity<List<MypagePostDTO>> getWrittenUserPostsList(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        List<MypagePostDTO> writtenUserPostsDTOList = mypageApiService.getWrittenUserPosts(userId);
        return new ResponseEntity<>(writtenUserPostsDTOList, HttpStatus.OK);
    }


    //6
    @Operation(
            summary = "\"내가 저장한 맛집 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 즐겨찾기해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/favorite-restaurant-list")
    public ResponseEntity<List<FavoriteRestaurantInfoDTO>> getFavoriteRestaurantList(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        List<FavoriteRestaurantInfoDTO> userFavoriteRestaurantList = mypageApiService.getUserFavoriteRestaurantList(userId);
        return new ResponseEntity<>(userFavoriteRestaurantList, HttpStatus.OK);
    }


    //7 커뮤니티
    @Operation(
            summary = "\"내가 저장한 커뮤니티 게시글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 저장해놓은 커뮤니티 게시글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-scrap-list")
    public ResponseEntity<List<MypagePostDTO>> getCommunityScrapList(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        List<MypagePostDTO> postScrapsDTO = mypageApiService.getScrappedUserPosts(userId);
        return new ResponseEntity<>(postScrapsDTO, HttpStatus.OK);
    }


    //8 커뮤니티
    @Operation(
            summary = "\"내가 작성한 커뮤니티 댓글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티의 댓글 리스트들을 불러옵니다."
    )
    @GetMapping("/auth/mypage/community-comment-list")
    public ResponseEntity<List<MypagePostCommentDTO>> getCommunityCommentList(
            @Parameter(hidden = true) @JwtToken Integer userId
    ){

        List<MypagePostCommentDTO> commentedUserPosts = mypageApiService.getCommentedUserPosts(userId);
        return new ResponseEntity<>(commentedUserPosts, HttpStatus.OK);
    }


//    //9
//    @Operation(
//            summary = "피드백 보내기 기능입니다",
//            description = "피드백을 보냅니다."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "피드백 감사합니다."),
//            @ApiResponse(responseCode = "400", description = "내용이 없습니다."),
//    })
//    @PostMapping("/auth/mypage/feedback")
//    public ResponseEntity<MypageErrorDTO> sendFeedback(
//            @Parameter(hidden = true) @JwtToken Integer userId,
//            @RequestBody FeedbackDTO feedbackDTO
//    ){
//        String comments = feedbackDTO.getComments();
//        MypageErrorDTO response = new MypageErrorDTO();
//
//        if (comments == null) {
//            response.setError("내용이 없습니다.");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
//        OFeedbackService.addApiFeedback(comments, userId);
//        response.setError("피드백 감사합니다.");
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }


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
