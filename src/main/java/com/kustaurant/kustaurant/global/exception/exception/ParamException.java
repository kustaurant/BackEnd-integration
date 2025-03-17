package com.kustaurant.kustaurant.global.exception.exception;

public class ParamException extends IllegalArgumentException{
    public ParamException() {
        super();
    }

    public ParamException(String s) {
        super(s);
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamException(Throwable cause) {
        super(cause);
    }
}
