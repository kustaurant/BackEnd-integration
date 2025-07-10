package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

/**
 * 요청이 잘못된 경우에 사용할 수 있는 에러입니다.
 */
public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
