package com.kustaurant.kustaurant.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* ── COMMON ── */
    INVALID_INPUT_VALUE   (HttpStatus.BAD_REQUEST,          "COMMON-001", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED    (HttpStatus.METHOD_NOT_ALLOWED,   "COMMON-002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR (HttpStatus.INTERNAL_SERVER_ERROR,"COMMON-003", "예상치 못한 서버 오류입니다."),

    /* ──── USER ──── */
    USER_NOT_FOUND        (HttpStatus.NOT_FOUND,            "USER-001",   "사용자를 찾을 수 없습니다."),

    /* ──── RESTAURANT ──── */
    RESTAURANT_NOT_FOUND  (HttpStatus.NOT_FOUND,            "RESTAURANT-001", "식당을 찾을 수 없습니다."),

    /* ──── AUTH ──── */
    UNAUTHORIZED (HttpStatus.UNAUTHORIZED, "AUTH-401-UNAUTHORIZED", "인증이 필요합니다."),
    AT_EXPIRED   (HttpStatus.UNAUTHORIZED, "AUTH-401-AT-EXPIRED",  "Access 토큰이 만료되었습니다."),
    AT_INVALID   (HttpStatus.UNAUTHORIZED, "AUTH-401-AT-INVALID",  "유효하지 않은 Access 토큰입니다."),
    RT_EXPIRED   (HttpStatus.UNAUTHORIZED, "AUTH-401-RT-EXPIRED",  "Refresh 토큰이 만료되었습니다."),
    RT_INVALID   (HttpStatus.UNAUTHORIZED, "AUTH-401-RT-INVALID",  "유효하지 않은 Refresh 토큰입니다."),
    ACCESS_DENIED (HttpStatus.FORBIDDEN, "AUTH-FORBIDDEN", "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String     code;
    private final String     message;
}
