package com.kustaurant.mainapp.user.mypage.controller.response.api;

public record ProfileUpdateResponse(
        String nickname,
        String email,
        String phoneNumber
) {}
