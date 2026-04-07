package com.kustaurant.kustaurant.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorResponse(
        int            status,   // HTTP status code
        String         code,     // OUR-001 같은 내부 식별자
        String         message,  // 사용자용 기본 메시지
        List<SpecifiedErrorResponse> errors  // Bean Validation 세부 오류
) {
    /** 검증 오류가 없을 때 */
    @Builder
    public static ApiErrorResponse of(ErrorCode ec) {
        return new ApiErrorResponse(
                ec.getStatus().value(),
                ec.getCode(),
                ec.getMessage(),
                List.of()
        );
    }

    /** Bean Validation 오류가 있을 때 */
    public static ApiErrorResponse of(ErrorCode ec, BindingResult br) {

        List<SpecifiedErrorResponse> details = br.getFieldErrors()
                .stream()
                .map(SpecifiedErrorResponse::of)
                .collect(Collectors.toList());

        return new ApiErrorResponse(
                ec.getStatus().value(),
                ec.getCode(),
                ec.getMessage(),
                details
        );
    }

    /** Controller method parameter validation 오류가 있을 때 */
    public static ApiErrorResponse of(ErrorCode ec, List<ParameterValidationResult> pvr) {
        List<SpecifiedErrorResponse> details = pvr.stream()
                .flatMap(result -> toSpecifiedErrors(result).stream())
                .collect(Collectors.toList());

        return new ApiErrorResponse(
                ec.getStatus().value(),
                ec.getCode(),
                ec.getMessage(),
                details
        );
    }

    private static List<SpecifiedErrorResponse> toSpecifiedErrors(ParameterValidationResult result) {
        String field = result.getMethodParameter().getParameterName();
        String value = String.valueOf(result.getArgument());

        return result.getResolvableErrors()
                .stream()
                .map(error -> new SpecifiedErrorResponse(field, value, error.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
