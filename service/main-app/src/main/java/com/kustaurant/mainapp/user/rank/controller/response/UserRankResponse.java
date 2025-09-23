package com.kustaurant.mainapp.user.rank.controller.response;


import com.kustaurant.mainapp.user.rank.infrastructure.UserRankProjection;
import com.kustaurant.mainapp.common.util.UserIconResolver;

public record UserRankResponse(
        Long userId,
        String nickname,
        String iconUrl,
        int evaluationCount,
        int rank
) {
    public static UserRankResponse from(UserRankProjection p) {
        return new UserRankResponse(
                p.getUserId(),
                p.getNickname(),
                UserIconResolver.resolve(p.getEvaluationCount()),
                p.getEvaluationCount(),
                p.getUserRank()
        );
    }
}
