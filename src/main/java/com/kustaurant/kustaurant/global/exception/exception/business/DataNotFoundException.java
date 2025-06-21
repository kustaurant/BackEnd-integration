package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

// 사용자 정의 예외
public class DataNotFoundException extends BusinessException {

    public DataNotFoundException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

    public DataNotFoundException(ErrorCode errorCode, int id, String type) {
        super(errorCode, "ID가 " + id + "인 " + type + "이(가) 존재하지 않습니다.");
    }
}
