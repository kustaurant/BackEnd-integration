package com.kustaurant.kustaurant.user.rank.controller;

import com.kustaurant.kustaurant.user.rank.controller.response.UserRankResponse;
import com.kustaurant.kustaurant.user.rank.service.RankingService;
import com.kustaurant.kustaurant.user.rank.domain.RankingSortOption;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RankingApiController {
    private final RankingService rankingService;

    // 1. 커뮤니티 메인 유저랭킹

    @Operation(summary = "커뮤니티 메인의 랭킹 탭에서 유저 랭킹 리스트 조회",
            description = "평가 수 기반의 유저 랭킹을 반환. 분기순, 최신순으로 랭킹을 산정할 수 있습니다. 평가를 1개 이상 한 유저들로 최대100명 랭킹이 매겨집니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = {@Content(array = @ArraySchema(schema = @Schema(implementation = UserRankResponse.class)))})
    @GetMapping("/v2/community/ranking")
    public List<UserRankResponse> ranking(
            @RequestParam(defaultValue = "CUMULATIVE") @Parameter(description = "랭킹 조회 기준(시즌별:SEASONAL, 누적순:CUMULATIVE)") RankingSortOption sort
    ) {
        return rankingService.getTop100(sort);
    }
}
