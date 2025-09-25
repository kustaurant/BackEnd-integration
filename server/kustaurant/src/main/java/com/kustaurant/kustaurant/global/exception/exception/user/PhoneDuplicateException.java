package com.kustaurant.kustaurant.global.exception.exception.user;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class PhoneDuplicateException extends BusinessException {
    public PhoneDuplicateException() {
        super(ErrorCode.PHONE_DUPLICATED);
    }
}
