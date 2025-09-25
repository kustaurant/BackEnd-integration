package com.kustaurant.kustaurant.global.exception.exception.auth;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class AccessTokenInvalidException extends JwtAuthException {
    public AccessTokenInvalidException() {
        super(ErrorCode.AT_INVALID);
    }

    public AccessTokenInvalidException(Throwable cause) {
        super(ErrorCode.AT_INVALID, cause);
    }
}
