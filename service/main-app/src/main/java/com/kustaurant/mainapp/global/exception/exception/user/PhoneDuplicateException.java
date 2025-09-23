package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class PhoneDuplicateException extends BusinessException {
    public PhoneDuplicateException() {
        super(ErrorCode.PHONE_DUPLICATED);
    }
}
