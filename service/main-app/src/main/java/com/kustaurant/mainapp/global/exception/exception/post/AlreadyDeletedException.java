package com.kustaurant.mainapp.global.exception.exception.post;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class AlreadyDeletedException extends BusinessException {
    public AlreadyDeletedException() {
        super(ErrorCode.POST_ALREADY_DELETED);
    }
}
