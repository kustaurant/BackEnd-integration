package com.kustaurant.mainapp.user.rank.controller;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.user.rank.controller.response.UserRankResponse;
import com.kustaurant.mainapp.user.rank.domain.RankingSortOption;
import com.kustaurant.mainapp.user.rank.service.RankingService;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class RankingController {
    private final RankingService rankingService;

    @GetMapping("/ranking")
    public String ranking(
            @RequestParam(name = "sort", defaultValue = "CUMULATIVE") RankingSortOption sort,
            Model model,
            @AuthUser AuthUserInfo user
    ) {
        // top100 목록
        List<UserRankResponse> rankList = rankingService.getTop100(sort);
        model.addAttribute("rankList", rankList);

        // 내 랭크 (top100 밖이어도 전체 기준으로 단건 조회)
        if (user != null && user.id() != null) {
            var myRankOpt = rankingService.getMyRank(sort, user.id());
            model.addAttribute("myRank", myRankOpt.orElse(null));

            boolean inTop100 = rankList.stream().anyMatch(it -> it.userId().equals(user.id()));
            model.addAttribute("inTop100", inTop100);
        } else {
            model.addAttribute("myRank", null);
            model.addAttribute("inTop100", false);
        }

        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", "ranking");
        return "user/ranking";
    }
}

