package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
