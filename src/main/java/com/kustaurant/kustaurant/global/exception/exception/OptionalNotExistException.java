package com.kustaurant.kustaurant.global.exception.exception;

public class OptionalNotExistException extends RuntimeException {
    public OptionalNotExistException() {
        super();
    }

    public OptionalNotExistException(String message) {
        super(message);
    }

    public OptionalNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptionalNotExistException(Throwable cause) {
        super(cause);
    }

    protected OptionalNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
