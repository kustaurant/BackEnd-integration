package com.kustaurant.kustaurant.common.user.controller.web;

import com.kustaurant.kustaurant.common.user.controller.port.MypageService;
import com.kustaurant.kustaurant.common.user.controller.web.response.MypageDataDTO;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.auth.webUser.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class MypageController {
    private final MypageService mypageService;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/myPage")
    public String myPage(
            @RequestParam(value = "menu-index", defaultValue = "0") int menuIndex,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            Model model
    ){
        User user = customOAuth2User.getUser();
        MypageDataDTO mypageData = mypageService.getMypageData(user.getId());

        // 메뉴 탭 인덱스 정보
        model.addAttribute("menuIndex", menuIndex);
        model.addAttribute("user", user);
        model.addAttribute("restaurantFavoriteList", mypageData.getRestaurantFavoriteList());
        model.addAttribute("restaurantEvaluationList", mypageData.getRestaurantEvaluationList());
        model.addAttribute("postList", mypageData.getPostList());
        model.addAttribute("postCommentList", mypageData.getPostCommentList());
        model.addAttribute("postScrabList", mypageData.getPostScrapList());

        return "mypage";
    }



    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PatchMapping("/api/myPage/updateProfile")
    public ResponseEntity<String> updateProfile(
            @RequestBody Map<String, String> requestBody,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        User user = customOAuth2User.getUser();
        String newNickname = requestBody.get("newNickname");
        String newPhoneNum = requestBody.get("newPhoneNum");

        if ((newNickname == null || newNickname.isBlank()) &&
                (newPhoneNum == null || newPhoneNum.isBlank())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("변경된 내용이 없습니다.");
        }

        try {
            // 닉네임이 입력되었다면 변경
            if (newNickname != null && !newNickname.isBlank()) {
                user.changeNickname(new Nickname(newNickname));
            }

            // 전화번호가 입력되었다면 변경
            if (newPhoneNum != null && !newPhoneNum.isBlank()) {
                user.changePhoneNumber(new PhoneNumber(newPhoneNum));
            }

            userRepository.save(user);
            return ResponseEntity.ok().build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
