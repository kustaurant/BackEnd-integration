package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class RefreshTokenExpiredException extends JwtAuthException {
    public RefreshTokenExpiredException(String message) {
        super(ErrorCode.RT_EXPIRED);
    }
}
