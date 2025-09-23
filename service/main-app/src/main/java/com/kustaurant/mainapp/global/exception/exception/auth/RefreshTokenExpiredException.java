package com.kustaurant.mainapp.global.exception.exception.auth;

import com.kustaurant.mainapp.global.exception.ErrorCode;

public class RefreshTokenExpiredException extends JwtAuthException {
    public RefreshTokenExpiredException(String message) {
        super(ErrorCode.RT_EXPIRED);
    }
}
