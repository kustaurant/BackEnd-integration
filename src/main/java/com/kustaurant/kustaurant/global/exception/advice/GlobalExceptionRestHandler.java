//package com.kustaurant.kustaurant.global.exception.advice;
//
//import com.kustaurant.kustaurant.global.exception.ErrorResponse;
//import com.kustaurant.kustaurant.global.exception.exception.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
//
//import java.nio.file.AccessDeniedException;
//
///**
// * @author Ding
// * @since 2024.7.10.
// * description: 프로젝트 전역의 RestController에서 발생할 수 있는 오류에 대한 예외 처리를 하는 클래스입니다.
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalExceptionRestHandler {
//
//    @ExceptionHandler(DataNotFoundException.class)
//    public ResponseEntity<ErrorResponse> dataNotFound(DataNotFoundException e) {
//        log.error("[DataNotFoundException]: {}", e.getMessage());
//        return new ResponseEntity<>(new ErrorResponse("NOT FOUND", e.getMessage()), HttpStatus.NOT_FOUND);
//    }
//
//    /**
//     * description: RestController의 함수에서 @RequestParam으로 받은 인자가 형식이 안 맞을 경우 호출됩니다.
//     * example: Integer인데 String값이 들어옴. enum 클래스에 없는 값이 들어옴.
//     */
////    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
////    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
////        String errorMessage = "Invalid parameter: " + e.getName() + " should be of type " + e.getRequiredType().getSimpleName();
////        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
////    }
//
////    @ExceptionHandler(ParamException.class)
////    public ResponseEntity<ErrorResponse> handleTierParamException(ParamException e) {
////        log.error("[TierParamException]", e);
////        return new ResponseEntity<>(new ErrorResponse("BAD REQUEST", e.getMessage()), HttpStatus.BAD_REQUEST);
////    }
////
////    @ExceptionHandler(OptionalNotExistException.class)
////    public ResponseEntity<ErrorResponse> handleOptionalNotExistException(OptionalNotExistException e) {
////        log.error("[OptionalNotExistException]", e);
////        return new ResponseEntity<>(new ErrorResponse("NOT FOUND", e.getMessage()), HttpStatus.NOT_FOUND);
////    }
//
////    @ExceptionHandler(IllegalStateException.class)
////    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
////        log.error("[IllegalStateException]", e);
////        return new ResponseEntity<>(new ErrorResponse("SERVER STATE ERROR", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
////    }
//
////    @ExceptionHandler(ServerException.class)
////    public ResponseEntity<ErrorResponse> handleIllegalStateException(ServerException e) {
////        log.error("[ServerException]", e);
////        return new ResponseEntity<>(new ErrorResponse("SERVER ERROR", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
////    }
//
//    //띵운씨 이런거 추가했으면 전체적으로 브리핑좀 해주세요 ^^
////
////    @ExceptionHandler(RuntimeException.class)
////    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
////        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
////    }
//
////    @ExceptionHandler(IllegalArgumentException.class)
////    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
////        log.error("[IllegalArgumentException]", e);
////        return new ResponseEntity<>(new ErrorResponse("ARGUMENT ERROR", e.getMessage()), HttpStatus.BAD_REQUEST);
////
////    }
//
//}
