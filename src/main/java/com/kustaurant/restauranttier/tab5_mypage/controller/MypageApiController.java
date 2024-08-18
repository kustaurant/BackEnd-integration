package com.kustaurant.restauranttier.tab5_mypage.controller;

import com.kustaurant.restauranttier.common.apiUser.JwtToken;
import com.kustaurant.restauranttier.tab5_mypage.service.MypageApiService;
import com.kustaurant.restauranttier.tab5_mypage.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/mypage/")
public class MypageApiController {

    private final MypageApiService mypageApiService;


    //1
    @Operation(
            summary = "\"마이페이지 화면\" 로드에 필요한 정보 불러오기",
            description = "마이페이지 화면에 필요한 정보들이 반환됩니다."
    )
    @GetMapping
    public ResponseEntity<MypageMainDTO> getMypageView(@JwtToken Integer userId){

        MypageMainDTO mypageMainDTO = mypageApiService.getMypageInfo(userId);
        return new ResponseEntity<>(mypageMainDTO, HttpStatus.OK);
    }

    //2
    @Operation(
            summary = "마이페이지 프로필 정보(변경)화면 로드에 정보 불러오기",
            description = ""
    )
    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getMypageProfile(@JwtToken Integer userId){

        ProfileDTO profileDTO = mypageApiService.getProfileInfo(userId);
        return new ResponseEntity<>(profileDTO, HttpStatus.OK);
    }

    //3
    @Operation(
            summary = "마이페이지 프로필 변경하기",
            description = "유저의 닉네임,전화번호를 변경합니다. (프로필 사진 아직 미구현)"
    )
    @PatchMapping("/profile")
    public ResponseEntity<?> updateMypageProfile(
            @JwtToken Integer userId,
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
    @GetMapping("/evaluate-restuarnt-list")
    public ResponseEntity<List<EvaluateRestaurantInfoDTO>> getEvaluateRestaurantList(
            @JwtToken Integer userId
    ){

        List<EvaluateRestaurantInfoDTO> userEvaluateRestaurantList = mypageApiService.getUserEvaluateRestaurantList(userId);
        return new ResponseEntity<>(userEvaluateRestaurantList, HttpStatus.OK);
    }

    //5
    @Operation(
            summary = "\"내가 작성한 커뮤니티글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티 글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("/community-list")
    public ResponseEntity<List<MypagePostDTO>> getWrittenUserPostsList(@JwtToken Integer userId){

        List<MypagePostDTO> writtenUserPostsDTOList = mypageApiService.getWrittenUserPosts(userId);
        return new ResponseEntity<>(writtenUserPostsDTOList, HttpStatus.OK);
    }

    //6
    @Operation(
            summary = "\"내가 저장한 맛집 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 즐겨찾기해논 맛집 정보들을 불러옵니다."
    )
    @GetMapping("/favorite-restuarnt-list")
    public ResponseEntity<List<FavoriteRestaurantInfoDTO>> getFavoriteRestaurantList(
            @JwtToken Integer userId
    ){

        List<FavoriteRestaurantInfoDTO> userFavoriteRestaurantList = mypageApiService.getUserFavoriteRestaurantList(userId);
        return new ResponseEntity<>(userFavoriteRestaurantList, HttpStatus.OK);
    }

    //7
    @Operation( //---------------------------------------------------------------//
            summary = "\"내가 저장한 커뮤니티 게시글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 저장해놓은 커뮤니티 게시글 리스트 정보들을 불러옵니다."
    )
    @GetMapping("community-scrap-list")
    public ResponseEntity<List<MypagePostDTO>> getCommunityScrapList(
            @JwtToken Integer userId
    ){

        List<MypagePostDTO> postScrapsDTO = mypageApiService.getScrappedUserPosts(userId);
        return new ResponseEntity<>(postScrapsDTO, HttpStatus.OK);
    }

    //8
    @Operation(
            summary = "\"내가 작성한 커뮤니티 댓글 화면\" 로드에 필요한 정보 불러오기",
            description = "유저가 작성한 커뮤니티의 댓글 리스트들을 불러옵니다."
    )
    @GetMapping("community-comment-list")
    public ResponseEntity<List<MypagePostCommentDTO>> getCommunityCommentList(
            @JwtToken Integer userId
    ){

        List<MypagePostCommentDTO> commentedUserPosts = mypageApiService.getCommentedUserPosts(userId);
        return new ResponseEntity<>(commentedUserPosts, HttpStatus.OK);
    }
}
