package com.kustaurant.kustaurant.user.user.domain;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE,   // 정상 회원
    DELETED;  // 탈퇴한 회원
}
