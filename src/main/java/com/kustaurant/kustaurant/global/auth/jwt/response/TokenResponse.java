package com.kustaurant.kustaurant.global.auth.jwt.response;

import lombok.AllArgsConstructor;
import lombok.Getter;


public record TokenResponse(
        String accessToken,
        String refreshToken
) {}
