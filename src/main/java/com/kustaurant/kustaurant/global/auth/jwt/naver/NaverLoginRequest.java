package com.kustaurant.kustaurant.global.auth.jwt.naver;

import jakarta.validation.constraints.NotBlank;

public record NaverLoginRequest (
    @NotBlank
    String provider,
    @NotBlank
    String providerId,
    @NotBlank
    String naverAccessToken
){}