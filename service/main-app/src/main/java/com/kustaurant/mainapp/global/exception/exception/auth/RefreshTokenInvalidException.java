package com.kustaurant.mainapp.global.exception.exception.auth;

import com.kustaurant.mainapp.global.exception.ErrorCode;

public class RefreshTokenInvalidException extends JwtAuthException {
    public RefreshTokenInvalidException() {
        super(ErrorCode.RT_INVALID);
    }
}
