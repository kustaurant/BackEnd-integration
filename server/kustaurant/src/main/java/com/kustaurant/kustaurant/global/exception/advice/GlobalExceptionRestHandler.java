package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.common.discordAlert.DiscordNotifier;
import com.kustaurant.kustaurant.global.exception.ApiErrorResponse;
import com.kustaurant.kustaurant.global.exception.exception.BusinessException;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.auth.JwtAuthException;
import com.kustaurant.kustaurant.global.exception.exception.user.ProviderApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionRestHandler {
    private final DiscordNotifier discordNotifier;

    /**   1. Bean Validation 오류(4xx)   */
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

    /**   2. 지원되지 않는 HTTP 메서드(405)   */
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

    /**   3. 도메인 비즈니스 예외(4xx)   */
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

    /**   4. ArgumentResolver 용 (@AuthUser) (4xx)   */
    @ExceptionHandler(JwtAuthException.class)
    protected ResponseEntity<ApiErrorResponse> handleJwtAuth(JwtAuthException ex) {
        ErrorCode ec = ex.getErrorCode();
        return ResponseEntity
                .status(ec.getStatus())
                .body(ApiErrorResponse.of(ec));
    }

    /**   5. 로그인 외부 호출 api 실패 예외(5xx)   */
    @ExceptionHandler(ProviderApiException.class)
    protected ResponseEntity<ApiErrorResponse> handleProviderFail(
            ProviderApiException ex, HttpServletRequest req
    ) {
        HttpStatus status = ErrorCode.PROVIDER_API_FAIL.getStatus();
        notifyIf5xx(ex, req, status);
        return ResponseEntity.status(status).body(ApiErrorResponse.of(ErrorCode.PROVIDER_API_FAIL));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ApiErrorResponse> handleMissingRequestHeader(
            MissingRequestHeaderException ex, HttpServletRequest req
    ) {
        HttpStatus status = HttpHeaders.AUTHORIZATION.equalsIgnoreCase(ex.getHeaderName())
                ? HttpStatus.UNAUTHORIZED : HttpStatus.BAD_REQUEST;

        log.warn("[MissingHeader] {} {} header='{}'", req.getMethod(), req.getRequestURI(), ex.getHeaderName());

        return ResponseEntity.status(status).body(ApiErrorResponse.of(
                status == HttpStatus.UNAUTHORIZED ?
                        ErrorCode.UNAUTHORIZED : ErrorCode.MISSING_REQUEST_HEADER));
    }

    /**   7. 그 외 모든 예외   */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponse> handleUnhandled(
            Exception ex, HttpServletRequest req
    ) {
        log.error("[Unhandled] {} {}", req.getMethod(), req.getRequestURI(), ex);
        HttpStatus status = ErrorCode.INTERNAL_SERVER_ERROR.getStatus();
        notifyIf5xx(ex, req, status);

        return ResponseEntity.status(status).body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /** --- 내부 공용 헬퍼: 상태가 5xx일 때만 디스코드 알림 --- */
    private void notifyIf5xx(Exception ex,
                             HttpServletRequest req,
                             HttpStatus status
    ) {
        if (!status.is5xxServerError()) return;
        String traceId = MDC.get("traceId");

        // 상태코드와 API 메시지를 함께 전달
        discordNotifier.send5xx(ex, req, (traceId == null ? "-" : traceId), status.value());
    }
}
