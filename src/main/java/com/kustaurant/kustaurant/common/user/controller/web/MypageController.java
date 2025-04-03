package com.kustaurant.kustaurant.common.user.controller.web;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.RestaurantComment;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.comment.PostComment;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrap;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.favorite.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.global.webUser.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class MypageController {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OUserRepository OUserRepository;

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/myPage")
    public String myPage(
            @RequestParam(value = "menu-index", defaultValue = "0") int menuIndex,
            Model model,
            Principal principal
    ){
        UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());

        model.addAttribute("user", UserEntity);

        // 메뉴 탭 인덱스 정보
        model.addAttribute("menuIndex", menuIndex);
        // 저장된 맛집 정보
        List<RestaurantFavoriteEntity> favoriteList =  UserEntity.getRestaurantFavoriteList();
        favoriteList.sort(Comparator.comparing(RestaurantFavoriteEntity::getCreatedAt).reversed()); //정렬
        model.addAttribute("restaurantFavoriteList", UserEntity.getRestaurantFavoriteList());
        // 평가한 맛집 정보
        List<Evaluation> evaluationList =  UserEntity.getEvaluationList();
        evaluationList.sort(Comparator.comparing((Evaluation e) -> { // 정렬
            if (e.getUpdatedAt() != null && e.getUpdatedAt().isAfter(e.getCreatedAt())) {
                return e.getUpdatedAt();
            }
            return e.getCreatedAt();
        }).reversed());
        model.addAttribute("restaurantEvaluationList", evaluationList);
        // 맛집 댓글 정보
        List<RestaurantComment> activeRestaurantCommentList =
                UserEntity.getRestaruantCommentList().stream()
                        .filter(restaurantcomment -> restaurantcomment.getStatus().equals("ACTIVE"))
                        .sorted(Comparator.comparing(RestaurantComment::getCreatedAt).reversed())
                        .toList();
        model.addAttribute("restaurantCommentList", activeRestaurantCommentList);

        List<PostEntity> activePostList = UserEntity.getPostList().stream()
                .filter(post -> post.getStatus().equals("ACTIVE"))
                .sorted(Comparator.comparing(PostEntity::getCreatedAt).reversed()) // 최신 글 순으로 정렬
                .toList();

        List<PostComment> activePostCommentList= UserEntity.getPostCommentList().stream()
                        .filter(postComment -> postComment.getStatus().equals("ACTIVE"))
                        .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed()) // 최신 글 순으로 정렬
                        .toList();

        List<PostScrap> postScraps= UserEntity.getScrapList().stream()
                .sorted(Comparator.comparing(PostScrap::getCreatedAt).reversed())
                .toList();


        model.addAttribute("postList",activePostList);
        model.addAttribute("postCommentList",activePostCommentList);
        model.addAttribute("postScrabList",postScraps);


        return "mypage";
    }

    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @PatchMapping("/api/myPage/setNickname")
    public ResponseEntity<String> setNickname(
            @RequestBody Map<String, String> requestBody,
            Principal principal
    ){
        String newNickname=requestBody.get("newNickname");
        String newPhoneNum=requestBody.get("newPhoneNum");
        UserEntity UserEntity = customOAuth2UserService.getUser(principal.getName());

        // 닉네임과 전화번호가 변경되지 않았을 경우
        if ((newNickname.equals(UserEntity.getUserNickname()) || newNickname.equals("")) &&
                (newPhoneNum.equals(UserEntity.getPhoneNumber()) || newPhoneNum.equals(""))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("변경된 내용이 없습니다.");
        }


        // 닉네임이 변경되었는지 확인
        if (!newNickname.equals(UserEntity.getUserNickname())&&newNickname!="") {
            // 닉네임 변경 후 30일이 안 지났는지 확인
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            if (UserEntity.getUpdatedAt() != null && UserEntity.getUpdatedAt().isAfter(thirtyDaysAgo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("닉네임을 변경한 지 30일이 지나지 않아 변경할 수 없습니다.");
            }
            //전과 동일한 닉네임
            if (newNickname.toLowerCase().equals(UserEntity.getUserNickname().getValue().toLowerCase())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이전과 동일한 닉네임입니다.");
            }
            // 닉네임이 2글자 이하인 경우
            if (newNickname.length() < 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임은 2자 이상이어야 합니다.");
            }
            // 닉네임이 10자 이상인 경우
            if (newNickname.length() > 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임은 10자 이하여야 합니다.");
            }
            // 닉네임이 이미 존재하는 경우
            Optional<UserEntity> userOptional = OUserRepository.findByUserNickname(newNickname);
            if (userOptional.isPresent() && !newNickname.equals(UserEntity.getUserNickname())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 닉네임이 이미 존재합니다.");
            }

            UserEntity.setUserNickname(new Nickname(newNickname));
            UserEntity.setUpdatedAt(LocalDateTime.now());

        }

        // 전화번호가 변경되었는지 확인
        if (!newPhoneNum.equals(UserEntity.getPhoneNumber())) {
            if (newPhoneNum.matches("\\d{11}")) {
                UserEntity.setPhoneNumber(new PhoneNumber(newPhoneNum));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("전화번호는 숫자로 11자로만 입력되어야 합니다.");
            }
        }



        OUserRepository.save(UserEntity);
        return ResponseEntity.ok().build();
    }

}
