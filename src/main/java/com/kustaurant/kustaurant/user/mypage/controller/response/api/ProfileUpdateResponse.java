package com.kustaurant.kustaurant.user.mypage.controller.response.api;

public record ProfileUpdateResponse(
        String nickname,
        String email,
        String phoneNumber
) {}
