package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class RefreshTokenInvalidException extends JwtAuthException {
    public RefreshTokenInvalidException(String message) {
        super(ErrorCode.RT_INVALID);
    }
}
