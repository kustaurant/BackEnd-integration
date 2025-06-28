package com.kustaurant.kustaurant.user.mypage.infrastructure.projection;

public record MypageMainProjection(
        String nickname,
        int evalCnt,
        int postCnt
) {
}
