package com.kustaurant.kustaurant.user.rank.controller.response;


import com.kustaurant.kustaurant.user.rank.infrastructure.UserRankProjection;
import com.kustaurant.kustaurant.common.util.UserIconResolver;

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
