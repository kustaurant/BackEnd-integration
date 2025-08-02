package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.global.exception.exception.business.BusinessException;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.auth.JwtAuthException;
import com.kustaurant.kustaurant.global.exception.exception.business.ProviderApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionRestHandler {

    /**   1. Bean Validation 오류   */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handleInvalidInput(
            MethodArgumentNotValidException ex
    ) {
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                ex.getBindingResult()
        );

        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(errorResponse);
    }

    /**   2. 지원되지 않는 HTTP 메서드   */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest req
    ) {
        ApiErrorResponse errorResponse = ApiErrorResponse.of(
                ErrorCode.METHOD_NOT_ALLOWED
        );

        log.warn("[MethodNotAllowed] {} {}", req.getMethod(), req.getRequestURI(), ex);

        return ResponseEntity.status(ErrorCode.METHOD_NOT_ALLOWED.getStatus()).body(errorResponse);
    }

    /**   3. 도메인 비즈니스 예외   */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest req
    ) {
        ErrorCode errorCode = ex.getErrorCode();

        log.error("[BusinessException] {} {}", req.getMethod(), req.getRequestURI(), ex);

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiErrorResponse.of(errorCode));
    }

    /**   4. ArgumentResolver 용 (@AuthUser)   */
    @ExceptionHandler(JwtAuthException.class)
    public ResponseEntity<ApiErrorResponse> handleJwtAuth(JwtAuthException ex) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
                .status(ec.getStatus())
                .body(ApiErrorResponse.of(ec));
    }

    /**   5. 로그인 외부 호출 api 실패 예외   */
    @ExceptionHandler(ProviderApiException.class)
    public ResponseEntity<ApiErrorResponse> handleProviderFail(ProviderApiException ex) {
        return ResponseEntity.status(ErrorCode.PROVIDER_API_FAIL.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.PROVIDER_API_FAIL));
    }

    /**   6. 그 외 모든 예외   */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponse> handleUnhandled(
            Exception ex,
            HttpServletRequest req) {

        log.error("[Unhandled] {} {}", req.getMethod(), req.getRequestURI(), ex);

        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
