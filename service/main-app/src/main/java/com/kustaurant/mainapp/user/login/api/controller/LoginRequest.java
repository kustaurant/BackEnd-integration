package com.kustaurant.mainapp.user.login.api.controller;

import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @Schema(description = "로그인 제공자 (NAVER 또는 APPLE)", example = "NAVER")
        @NotNull LoginApi provider,   // enum NAVER, APPLE ...
        @Schema(description = "NAVER만 사용, APPLE은 null로 보내주세요")
        String providerId,             // 네이버: 사용  애플: null
        @Schema(description = "네이버: accessToken, 애플: identityToken 을 보내주세요")
        @NotBlank String token,        // 네이버: accessToken,  애플: identityToken
        @Schema(description = "APPLE만 사용, NAVER는 null로 보내주세요")
        String authCode                // 네이버: null  애플: 사용
) {
}
