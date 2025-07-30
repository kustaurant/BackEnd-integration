package com.kustaurant.kustaurant.user.rank;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class RankingController {
    private final UserRepository userRepository;

    @GetMapping("/ranking")
    public String ranking(
            Model model,
            @AuthUser AuthUserInfo user
    ) {
        // 평가 1개 이상 한 유저 리스트
        List<User> UserEntityList = userRepository.findUsersWithEvaluationCountDescending();
        // 순위 리스트
        List<UserRank> userRankList = calculateRank(UserEntityList);
        model.addAttribute("rankList", userRankList);
        // 로그인 상태일 경우
        if (user.id() != null) {
            boolean isAdded = false;
            for (UserRank userRank :
                    userRankList) {
                if (userRank.getUser().getId().equals(user.id())) {
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
        return "user/ranking";
    }

    private List<UserRank> calculateRank(List<User> userList) {
        List<UserRank> rankList = new ArrayList<>();

        int i = 0;
        int prevCount = 100000; // 이전 유저의 평가 개수
        int countSame = 1; // 동일 순위를 세기 위한 변수
        for (User user : userList) {
            UserRank userRank = new UserRank();
            userRank.setUser(user);
            int evaluationCount = user.getEvalCount();
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

