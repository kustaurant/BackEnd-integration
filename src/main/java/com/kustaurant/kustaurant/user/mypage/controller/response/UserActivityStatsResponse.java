package com.kustaurant.kustaurant.user.mypage.controller.response;

public record UserActivityStatsResponse(
        int savedRestCnt,
        int ratedRestCnt,
        int commPostCnt,
        int commCommentCnt,
        int commSavedPostCnt
) {}
