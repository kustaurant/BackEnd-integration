package com.kustaurant.mainapp.user.login.api.controller.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
