package com.kustaurant.kustaurant.user.login.api.controller;

import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank LoginApi provider,   // enum NAVER, APPLE ...
        String providerId,                 // 네이버: 사용  애플: null
        @NotBlank String token,            // 네이버: accessToken,  애플: identityToken
        String authCode                    // 네이버: null  애플: 사용
) {
}
