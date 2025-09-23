package com.kustaurant.mainapp.global.exception.exception;

import com.kustaurant.mainapp.global.exception.ErrorCode;

public class ApiStatusException extends BusinessException {

    public ApiStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
