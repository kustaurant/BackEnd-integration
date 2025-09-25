package com.kustaurant.kustaurant.global.exception.exception.post;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;

public class AlreadyDeletedException extends BusinessException {
    public AlreadyDeletedException() {
        super(ErrorCode.POST_ALREADY_DELETED);
    }
}
