package com.kustaurant.kustaurant.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        int            status,   // HTTP status code
        String         code,     // OUR-001 같은 내부 식별자
        String         message,  // 사용자용 기본 메시지
        List<SpecifiedError> errors  // Bean Validation 세부 오류
) {

    /** 필드 검증 오류 DTO */
    public record SpecifiedError(String field, String value, String reason) {
        static SpecifiedError of(FieldError e) {
            return new SpecifiedError(
                    e.getField(),
                    String.valueOf(e.getRejectedValue()),
                    e.getDefaultMessage()
            );
        }
    }

    /** 검증 오류가 없을 때 */
    @Builder
    public static ApiErrorResponse of(ErrorCode ec) {
        return new ApiErrorResponse(
                ec.getStatus().value(),
                ec.getCode(),
                ec.getMessage(),
                null
        );
    }

    /** Bean Validation 오류가 있을 때 */
    public static ApiErrorResponse of(ErrorCode ec,
                                      BindingResult br) {

        List<SpecifiedError> details = br.getFieldErrors()
                .stream()
                .map(SpecifiedError::of)
                .collect(Collectors.toList());

        return new ApiErrorResponse(
                ec.getStatus().value(),
                ec.getCode(),
                ec.getMessage(),
                details
        );
    }
}

