package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class NoProfileChangeException extends BusinessException {
    public NoProfileChangeException() {
        super(ErrorCode.NO_PROFILE_CHANGE);
    }
}
