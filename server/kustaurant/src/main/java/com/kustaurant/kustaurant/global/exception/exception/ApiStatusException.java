package com.kustaurant.kustaurant.global.exception.exception;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class ApiStatusException extends BusinessException {

    public ApiStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
