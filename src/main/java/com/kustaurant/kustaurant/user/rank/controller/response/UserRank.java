package com.kustaurant.kustaurant.user.rank.controller.response;


public record UserRank(
        Long userId,
        String nickname,
        String iconUrl,
        int evaluationCount,
        int rank
) {}
