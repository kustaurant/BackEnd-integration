package com.kustaurant.kustaurant.user.mypage.controller.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustaurant.kustaurant.common.util.UserIconResolver;

public record ProfileResponse(
        String nickname,
        int savedRestaurantCnt,
        int evalCnt,
        int postCnt,
        int postCommentCnt,
        int savedPostCnt,
        String email,
        String phoneNumber
) {
    @JsonProperty("iconUrl")
    public String iconUrl() {
        return UserIconResolver.resolve(evalCnt);
    }
}
