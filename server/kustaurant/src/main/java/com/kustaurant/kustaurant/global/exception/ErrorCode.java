package com.kustaurant.kustaurant.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /* ── COMMON ── */
    INVALID_INPUT_VALUE   (HttpStatus.BAD_REQUEST,           "COMMON-001", "잘못된 입력 값입니다."),
    METHOD_NOT_ALLOWED    (HttpStatus.METHOD_NOT_ALLOWED,    "COMMON-002", "허용되지 않은 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR (HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-003", "예상치 못한 서버 오류입니다."),
    IMAGE_UPLOAD_FAIL     (HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-004", "이미지 업로드에 실패했습니다."),
    GONE (HttpStatus.GONE, "COMMON-005", "더 이상 지원하지 않는 기능입니다."),
    SERVICE_UNAVAILABLE (HttpStatus.SERVICE_UNAVAILABLE, "COMMON-006", "해당 기능 점검중입니다."),
    MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST, "COMMON-007", "필수 요청 헤더가 누락되었습니다."),

    /* ──── USER ──── */
    USER_NOT_FOUND        (HttpStatus.NOT_FOUND,            "USER-001",   "유저를 찾을 수 없습니다."),
    NICKNAME_COOLDOWN       (HttpStatus.BAD_REQUEST,    "USER-002", "닉네임 변경은 30일에 한 번만 가능합니다."),
    NICKNAME_DUPLICATED     (HttpStatus.CONFLICT,       "USER-003", "이미 사용 중인 닉네임 입니다."),
    PHONE_DUPLICATED        (HttpStatus.CONFLICT,       "USER-004", "이미 사용 중인 전화번호 입니다."),
    NO_PROFILE_CHANGE     (HttpStatus.BAD_REQUEST,    "USER-005", "요청에 변경된 값이 없습니다."),


    /* ──── RESTAURANT ──── */
    RESTAURANT_NOT_FOUND  (HttpStatus.NOT_FOUND,            "RESTAURANT-001", "식당을 찾을 수 없습니다."),
    RESTAURANT_COMMENT_NOT_FOUND (HttpStatus.NOT_FOUND, "RESTAURANT-002", "식당 대댓글을 찾을 수 없습니다."),
    RESTAURANT_FAVORITE_NOT_FOUND (HttpStatus.NOT_FOUND, "RESTAURANT-003", "식당 즐겨찾기를 찾을 수 없습니다."),

    /* ──── EVALUATION ──── */
    EVALUATION_NOT_FOUND (HttpStatus.NOT_FOUND, "EVALUATION-001", "평가를 찾을 수 없습니다."),
    RESTAURANT_SITUATION_RELATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RELATION-001", "레스토랑 상황을 찾을 수 없습니다"),

    /* ──── POST ──── */
    POST_NOT_FOUND        (HttpStatus.NOT_FOUND  , "POST-001", "게시글을 찾을 수 없습니다."),
    POST_ALREADY_DELETED  (HttpStatus.CONFLICT   , "POST-003", "이미 삭제된 게시글입니다."),

    /* ──── COMMENT ──── */
    COMMENT_NOT_FOUND (HttpStatus.NOT_FOUND, "COMMENT-001", "댓글을 찾을 수 없습니다."),

    /* ──── AUTH ──── */
    UNAUTHORIZED (HttpStatus.UNAUTHORIZED, "AUTH-401-UNAUTHORIZED", "인증이 필요합니다."),
    AT_EXPIRED   (HttpStatus.UNAUTHORIZED, "AUTH-401-AT-EXPIRED",  "Access 토큰이 만료되었습니다."),
    AT_INVALID   (HttpStatus.UNAUTHORIZED, "AUTH-401-AT-INVALID",  "유효하지 않은 Access 토큰입니다."),
    RT_EXPIRED   (HttpStatus.UNAUTHORIZED, "AUTH-401-RT-EXPIRED",  "Refresh 토큰이 만료되었습니다."),
    RT_INVALID   (HttpStatus.UNAUTHORIZED, "AUTH-401-RT-INVALID",  "유효하지 않은 Refresh 토큰입니다."),
    ACCESS_DENIED (HttpStatus.FORBIDDEN, "AUTH-FORBIDDEN", "접근 권한이 없습니다."),

    /* ── EXTERNAL PROVIDER ── */
    PROVIDER_API_FAIL   (HttpStatus.SERVICE_UNAVAILABLE,  "PROVIDER-001", "외부 로그인 공급자 API 서버에 문제가 있습니다."),
    PROVIDER_NOT_VALID  (HttpStatus.BAD_REQUEST, "PROVIDER-002", "지원하지 않는 PROVIDER 입니다");

    private final HttpStatus status;
    private final String     code;
    private final String     message;
}
