package com.kustaurant.kustaurant.global.exception.exception.user;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class NicknameDuplicateException extends BusinessException {
    public NicknameDuplicateException() {
        super(ErrorCode.NICKNAME_DUPLICATED);
    }
}
