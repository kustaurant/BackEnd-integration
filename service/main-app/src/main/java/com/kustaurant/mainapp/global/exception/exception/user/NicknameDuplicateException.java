package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class NicknameDuplicateException extends BusinessException {
    public NicknameDuplicateException() {
        super(ErrorCode.NICKNAME_DUPLICATED);
    }
}
