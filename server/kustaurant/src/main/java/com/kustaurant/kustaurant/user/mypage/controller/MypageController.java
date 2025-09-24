package com.kustaurant.kustaurant.user.mypage.controller;

import com.kustaurant.kustaurant.user.mypage.controller.port.MypageService;
import com.kustaurant.kustaurant.user.mypage.controller.request.ProfileUpdateRequest;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.ProfileUpdateResponse;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.mypage.controller.response.web.MypageDataView;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/myPage")
    public String myPage(
            @RequestParam(value = "menu-index", defaultValue = "0") int menuIndex,
            @AuthUser AuthUserInfo user,
            Model model
    ){
        MypageDataView data = mypageService.getMypageWebData(user.id());

        // 메뉴 탭 인덱스 정보
        model.addAttribute("menuIndex", menuIndex);
        model.addAttribute("user", userService.getUserById(user.id()));
        model.addAttribute("restaurantFavoriteList", data.getRestaurantFavoriteList());
        model.addAttribute("restaurantEvaluationList", data.getRestaurantEvaluationList());
        model.addAttribute("postList", data.getPostList());
        model.addAttribute("postCommentList", data.getPostCommentList());
        model.addAttribute("postScrapList", data.getPostScrapList());

        return "user/mypage";
    }


    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PatchMapping("/api/myPage/updateProfile")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @RequestBody @Valid ProfileUpdateRequest req,
            @AuthUser AuthUserInfo user
    ) {
        ProfileUpdateResponse response = mypageService.updateUserProfile(user.id(), req);

        return ResponseEntity.ok(response);
    }
}
