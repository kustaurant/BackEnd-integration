package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.common.discordAlert.DiscordNotifier;
import com.kustaurant.kustaurant.global.exception.WebErrorResponse;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionWebHandler {
    private final DiscordNotifier discordNotifier;

    @ExceptionHandler(DataNotFoundException.class)
    public String handleDataNotFoundException(
            DataNotFoundException e,
            HttpServletRequest req,
            Model model
    ) {
        log.error("[DataNotFoundException] {} {}: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        model.addAttribute("message", "존재하지 않습니다.");
        return "error/not_found";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public WebErrorResponse handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("입력값이 올바르지 않습니다.");

        return new WebErrorResponse("BAD_REQUEST", msg);
    }

    /** AccessDenied는 Security 체인에게 맡김 */
    @ExceptionHandler(AccessDeniedException.class)
    public void rethrowAccessDenied(AccessDeniedException e) throws AccessDeniedException {
        throw e;
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception e, HttpServletRequest req, Model model
    ) {
        log.error("[Exception] {} {}: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        notifyIf5xx(e, req, HttpStatus.INTERNAL_SERVER_ERROR);

        model.addAttribute("message", "잠시 후 다시 시도해주세요.");
        return "error/error";
    }

    private void notifyIf5xx(Exception ex,
                             HttpServletRequest req,
                             HttpStatus status
    ) {
        if (!status.is5xxServerError()) return;
        String traceId = MDC.get("traceId");
        discordNotifier.send5xx(
                ex,
                req,
                (traceId == null ? "-" : traceId),
                status.value()
        );
    }
}
