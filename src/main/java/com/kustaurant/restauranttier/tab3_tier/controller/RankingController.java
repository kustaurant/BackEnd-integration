package com.kustaurant.restauranttier.tab3_tier.controller;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import com.kustaurant.restauranttier.tab5_mypage.service.CustomOAuth2UserService;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RankingController {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;

    @GetMapping("/ranking")
    public String ranking(
            Model model,
            Principal principal
    ) {

        // 평가 1개 이상한 유저 리스트
        List<User> userList = userRepository.findUsersWithEvaluationCountDescending();
        // 순위 리스트
        List<UserRank> userRankList = calculateRank(userList);
        model.addAttribute("rankList", userRankList);
        // 로그인 상태일 경우
        if (principal != null) {
            User user = customOAuth2UserService.getUser(principal.getName());
            boolean isAdded = false;
            for (UserRank userRank :
                    userRankList) {
                if (userRank.getUser().equals(user)) {
                    model.addAttribute("myRank", userRank);
                    isAdded = true;
                }
            }
            if (!isAdded) {
                model.addAttribute("myRank", null);
            }
        } else {
            model.addAttribute("myRank", null);
        }
        model.addAttribute("currentPage","ranking");
        return "ranking";
    }

    private List<UserRank> calculateRank(List<User> userList) {
        List<UserRank> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user :
                userList) {
            UserRank userRank = new UserRank();
            userRank.setUser(user);
            int evaluationCount = user.getEvaluationList().size();
            if (evaluationCount < prevCount) {
                i += countSame;
                userRank.setRank(i);
                countSame = 1;
            } else {
                userRank.setRank(i);
                countSame++;
            }
            rankList.add(userRank);
            prevCount = evaluationCount;
        }
        return rankList;
    }
}

@Getter
@Setter
@NoArgsConstructor
class UserRank {
    private Integer rank;
    private User user;
}
