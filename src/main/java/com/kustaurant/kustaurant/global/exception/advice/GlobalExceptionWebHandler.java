package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
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
            Model model
    ) {
        log.error("[DataNotFoundException]: {}", e.getMessage());

        model.addAttribute("message", e.getMessage());

        return "error/not_found";
    }
}
