package com.kustaurant.kustaurant.user.mypage.controller.response.api;

public record ProfileResponse(
        String nickname,
        String email,
        String phoneNumber
) {}
