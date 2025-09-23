package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class NoProfileChangeException extends BusinessException {
    public NoProfileChangeException() {
        super(ErrorCode.NO_PROFILE_CHANGE);
    }
}
