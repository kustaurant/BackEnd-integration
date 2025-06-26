package com.kustaurant.kustaurant.user.mypage.controller.response;

public record ProfileResponse(
        String nickname,
        String email,
        String phoneNumber
) {}
