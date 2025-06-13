package com.kustaurant.kustaurant.user.domain.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE,   // 정상 회원
    DELETED;  // 탈퇴한 회원
}
