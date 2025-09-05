package com.kustaurant.kustaurant.user.rank.controller.response;


import com.kustaurant.kustaurant.user.rank.infrastructure.UserRankProjection;
import com.kustaurant.kustaurant.common.util.UserIconResolver;

public record UserRank(
        Long userId,
        String nickname,
        String iconUrl,
        int evaluationCount,
        int rank
) {
    public static UserRank from(UserRankProjection p) {
        return new UserRank(
                p.getUserId(),
                p.getNickname(),
                UserIconResolver.resolve(p.getEvaluationCount()),
                p.getEvaluationCount(),
                p.getUserRank()
        );
    }
}
