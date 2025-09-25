package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class AccessTokenExpiredException extends JwtAuthException {
    public AccessTokenExpiredException() {
        super(ErrorCode.AT_EXPIRED);
    }
}
