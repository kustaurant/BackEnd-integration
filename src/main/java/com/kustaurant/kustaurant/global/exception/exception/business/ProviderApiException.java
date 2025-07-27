package com.kustaurant.kustaurant.global.exception.exception.business;

import com.kustaurant.kustaurant.global.exception.ErrorCode;

public class ProviderApiException extends BusinessException {
    public ProviderApiException() {super(ErrorCode.PROVIDER_API_FAIL);
    }

    public ProviderApiException(String provider, String msg, Throwable cause) {
        super(ErrorCode.PROVIDER_API_FAIL, "[" + provider + "] " + msg, cause);
    }

    public ProviderApiException(String provider, String msg) {
        super(ErrorCode.PROVIDER_API_FAIL, "[" + provider + "] " + msg);
    }
}
