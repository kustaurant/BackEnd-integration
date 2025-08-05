package com.kustaurant.kustaurant.global.exception.exception.user;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class NoProfileChangeException extends BusinessException {
    public NoProfileChangeException() {
        super(ErrorCode.NO_PROFILE_CHANGE);
    }
}
