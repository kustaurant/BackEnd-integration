package com.kustaurant.kustaurant.global.exception;

import org.springframework.validation.FieldError;

/** 필드 검증 오류 DTO */
public record SpecifiedErrorResponse(String field, String value, String reason) {
    static SpecifiedErrorResponse of(FieldError e) {
        return new SpecifiedErrorResponse(
                e.getField(),
                String.valueOf(e.getRejectedValue()),
                e.getDefaultMessage()
        );
    }
}