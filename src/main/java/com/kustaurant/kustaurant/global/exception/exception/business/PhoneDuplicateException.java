package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class PhoneDuplicateException extends BusinessException {
    public PhoneDuplicateException() {
        super(ErrorCode.PHONE_DUPLICATED);
    }
}
