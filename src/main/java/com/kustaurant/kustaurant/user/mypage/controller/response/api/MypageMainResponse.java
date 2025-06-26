package com.kustaurant.kustaurant.user.mypage.controller.response.api;

public record MypageMainResponse(
        String iconUrl,
        String nickname,
        int evalCnt,
        int postCnt
) {
}
