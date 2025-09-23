package com.kustaurant.mainapp.global.exception.exception.auth;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public abstract class JwtAuthException extends AuthenticationException {
    private final ErrorCode errorCode;
    protected JwtAuthException(ErrorCode ec) {
        super(ec.getMessage());
        this.errorCode = ec;
    }

    protected JwtAuthException(ErrorCode ec, String message) {
        super(message);
        this.errorCode = ec;
    }

    protected JwtAuthException(ErrorCode ec, Throwable cause) {
        super(ec.getMessage(), cause);
        this.errorCode = ec;
    }

}
