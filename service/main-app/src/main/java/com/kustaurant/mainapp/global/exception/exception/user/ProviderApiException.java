package com.kustaurant.mainapp.global.exception.exception.user;

import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.BusinessException;

public class ProviderApiException extends BusinessException {
    public ProviderApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ProviderApiException(String provider, String msg, Throwable cause) {
        super(ErrorCode.PROVIDER_API_FAIL, "[" + provider + "] " + msg, cause);
    }

    public ProviderApiException(String provider, String msg) {
        super(ErrorCode.PROVIDER_API_FAIL, "[" + provider + "] " + msg);
    }
}
