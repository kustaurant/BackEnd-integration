package com.kustaurant.mainapp.global.exception.exception;

import com.kustaurant.mainapp.global.exception.ErrorCode;

// 사용자 정의 예외
public class ImageUploadException extends BusinessException {
    public ImageUploadException(Throwable cause) {
        super(ErrorCode.IMAGE_UPLOAD_FAIL, ErrorCode.IMAGE_UPLOAD_FAIL.getMessage(), cause);
    }

}
