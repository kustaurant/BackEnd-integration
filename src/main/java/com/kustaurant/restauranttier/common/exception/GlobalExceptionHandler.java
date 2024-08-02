package com.kustaurant.restauranttier.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @author Ding
 * @since 2024.7.10.
 * description: 프로젝트 전역의 RestController에서 발생할 수 있는 오류에 대한 예외 처리를 하는 클래스입니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * description: RestController의 함수에서 @RequestParam으로 받은 인자가 형식이 안 맞을 경우 호출됩니다.
     * example: Integer인데 String값이 들어옴. enum 클래스에 없는 값이 들어옴.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage = "Invalid parameter: " + e.getName() + " should be of type " + e.getRequiredType().getSimpleName();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * description: parameter로 enum value가 올바르지 않은 것이 들어왔을 경우
     */
    @ExceptionHandler(InvalidEnumValueException.class)
    public ResponseEntity<String> handleInvalidEnumValueException(InvalidEnumValueException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
