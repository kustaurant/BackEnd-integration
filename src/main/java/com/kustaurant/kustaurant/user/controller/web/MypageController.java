package com.kustaurant.kustaurant.user.controller.web;

import com.kustaurant.kustaurant.user.controller.port.MypageService;
import com.kustaurant.kustaurant.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.controller.web.response.MypageDataDTO;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class MypageController {
    private final MypageService mypageService;
    private final UserService userService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myPage")
    public String myPage(
            @RequestParam(value = "menu-index", defaultValue = "0") int menuIndex,
            @AuthUser AuthUserInfo user,
            Model model
    ){
        MypageDataDTO data = mypageService.getMypageData(user.id());

        // 메뉴 탭 인덱스 정보
        model.addAttribute("menuIndex", menuIndex);
        model.addAttribute("user", userService.getActiveUserById(user.id()));
        model.addAttribute("restaurantFavoriteList", data.getRestaurantFavoriteList());
        model.addAttribute("restaurantEvaluationList", data.getRestaurantEvaluationList());
        model.addAttribute("postList", data.getPostList());
        model.addAttribute("postCommentList", data.getPostCommentList());
        model.addAttribute("postScrabList", data.getPostScrapList());

        return "mypage";
    }


//    @PreAuthorize("isAuthenticated() and hasRole('USER')")
//    @PatchMapping("/api/myPage/updateProfile")
//    public ResponseEntity<String> updateProfile(
//            @RequestBody Map<String, String> requestBody,
//            Principal principal
//    ) {
//        User user = customOAuth2User.getUser();
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
