package com.kustaurant.kustaurant.global.exception.advice;

import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(0) // RestHandler보다 이 예외 핸들러를 먼저 적용해서, @Controller로 들어온 요청인 경우는 여기서 우선 적용되게 함.
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionWebHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public String handleDataNotFoundException(
            DataNotFoundException e,
            Model model
    ) {
        log.error("[DataNotFoundException]: {}", e.getMessage());

        model.addAttribute("message", "존재하지 않습니다.");

        return "error/not_found";
    }
}
