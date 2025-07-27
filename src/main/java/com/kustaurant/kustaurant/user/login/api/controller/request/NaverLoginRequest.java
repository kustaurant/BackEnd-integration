package com.kustaurant.kustaurant.user.login.api.controller.request;

import jakarta.validation.constraints.NotBlank;

public record NaverLoginRequest (
    @NotBlank
    String providerId,
    @NotBlank
    String naverAccessToken
){}