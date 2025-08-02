package com.kustaurant.kustaurant.user.login.api.controller;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
