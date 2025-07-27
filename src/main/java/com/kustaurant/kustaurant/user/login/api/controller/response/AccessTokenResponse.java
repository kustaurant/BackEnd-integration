package com.kustaurant.kustaurant.user.login.api.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Access 토큰만 포함된 응답 DTO")
public record AccessTokenResponse(
        String accessToken
) {
}
