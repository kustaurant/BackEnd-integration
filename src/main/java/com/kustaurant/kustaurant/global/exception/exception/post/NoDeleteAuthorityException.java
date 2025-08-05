package com.kustaurant.kustaurant.global.exception.exception.post;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class NoDeleteAuthorityException extends BusinessException {
    public NoDeleteAuthorityException() {
        super(ErrorCode.POST_NO_AUTHORITY);
    }
}
