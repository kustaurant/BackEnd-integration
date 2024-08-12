package com.kustaurant.restauranttier.common.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 사용자 정의 예외
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "entity not found")
public class DataNotFoundException extends RuntimeException{
    
//    예외 클래스의 직렬화에 사용되는 serialVersionUID 필드를 선언
//    애플리케이션의 다른 버전 간의 호환성을 보장
    private static final long serialVersionUID = 1L;
    public DataNotFoundException(String message){
        super(message);
    }
}
