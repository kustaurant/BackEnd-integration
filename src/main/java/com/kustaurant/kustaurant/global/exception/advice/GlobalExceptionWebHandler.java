package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.global.exception.exception.business.InvalidRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
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
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionWebHandler {

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

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidRequestException(InvalidRequestException e) {
        return Map.of("message", e.getErrorCode().getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(
            AccessDeniedException e,
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {
        log.error("[AccessDeniedException] {} {}: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        res.sendRedirect(req.getContextPath() + "/user/login");
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            Exception e,
            HttpServletRequest req,
            Model model
    ) {
        log.error("[Exception] {} {}: {}", req.getMethod(), req.getRequestURI(), e.getMessage(), e);
        model.addAttribute("message", "잠시 후 다시 시도해주세요.");
        return "error/error";
    }
}
