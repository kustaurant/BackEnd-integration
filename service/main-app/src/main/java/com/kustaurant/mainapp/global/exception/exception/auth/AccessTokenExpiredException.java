package com.kustaurant.mainapp.global.exception.exception.auth;

import com.kustaurant.mainapp.global.exception.ErrorCode;

public class AccessTokenExpiredException extends JwtAuthException {
    public AccessTokenExpiredException() {
        super(ErrorCode.AT_EXPIRED);
    }
}
