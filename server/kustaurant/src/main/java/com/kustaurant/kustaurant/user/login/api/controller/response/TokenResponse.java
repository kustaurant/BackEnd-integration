package com.kustaurant.kustaurant.user.login.api.controller.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
