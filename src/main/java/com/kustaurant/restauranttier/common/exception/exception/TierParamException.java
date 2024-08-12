package com.kustaurant.restauranttier.common.exception.exception;

public class TierParamException extends IllegalArgumentException{
    public TierParamException() {
        super();
    }

    public TierParamException(String s) {
        super(s);
    }

    public TierParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public TierParamException(Throwable cause) {
        super(cause);
    }
}
