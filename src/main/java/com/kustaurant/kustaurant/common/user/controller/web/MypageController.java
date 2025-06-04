package com.kustaurant.kustaurant.common.user.controller.web;

import com.kustaurant.kustaurant.common.user.controller.port.MypageService;
import com.kustaurant.kustaurant.common.user.controller.web.response.MypageDataDTO;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.auth.webUser.CustomOAuth2User;
import com.kustaurant.kustaurant.global.auth.webUser.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class MypageController {
    private final MypageService mypageService;
    private final UserRepository userRepository;
    private final CustomOAuth2UserService customOAuth2UserService;

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/myPage")
    public String myPage(
            @RequestParam(value = "menu-index", defaultValue = "0") int menuIndex,
            Principal principal,
            Model model
    ){
        UserEntity userEntity = customOAuth2UserService.getUser(principal.getName());
        MypageDataDTO mypageData = mypageService.getMypageData(userEntity.getUserId());

        // 메뉴 탭 인덱스 정보
        model.addAttribute("menuIndex", menuIndex);
        model.addAttribute("user", userEntity);
        model.addAttribute("restaurantFavoriteList", mypageData.getRestaurantFavoriteList());
        model.addAttribute("restaurantEvaluationList", mypageData.getRestaurantEvaluationList());
        model.addAttribute("postList", mypageData.getPostList());
        model.addAttribute("postCommentList", mypageData.getPostCommentList());
        model.addAttribute("postScrabList", mypageData.getPostScrapList());

        return "mypage";
    }



//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
//    @PatchMapping("/api/myPage/updateProfile")
//    public ResponseEntity<String> updateProfile(
//            @RequestBody Map<String, String> requestBody,
//            Principal principal
//    ) {
////        User user = customOAuth2User.getUser();
//        UserEntity userEntity = customOAuth2UserService.getUser(principal.getName());
//        String newNickname = requestBody.get("newNickname");
//        String newPhoneNum = requestBody.get("newPhoneNum");
//
//        if ((newNickname == null || newNickname.isBlank()) &&
//                (newPhoneNum == null || newPhoneNum.isBlank())) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("변경된 내용이 없습니다.");
//        }
//
//        try {
//            // 닉네임이 입력되었다면 변경
//            if (newNickname != null && !newNickname.isBlank()) {
//                user.changeNickname(new Nickname(newNickname));
//            }
//
//            // 전화번호가 입력되었다면 변경
//            if (newPhoneNum != null && !newPhoneNum.isBlank()) {
//                user.changePhoneNumber(new PhoneNumber(newPhoneNum));
//            }
//
//            userRepository.save(user);
//            return ResponseEntity.ok().build();
//
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
}
