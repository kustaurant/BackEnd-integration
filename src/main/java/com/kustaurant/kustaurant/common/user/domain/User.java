package com.kustaurant.kustaurant.common.user.domain;

import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.global.webUser.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Integer id;
    private final Nickname nickname;
    private final PhoneNumber phoneNumber;
    private final String email;
    private final UserRole role;
    private final String providerId;
    private final String loginApi;
    private final String accessToken;
    private final String refreshToken;
    private final String status;
    private final LocalDateTime createdAt;

    @Builder
    public User(Integer id, Nickname nickname, PhoneNumber phoneNumber, String email, UserRole role, String providerId, String loginApi, String accessToken, String refreshToken, String status, LocalDateTime createdAt) {
        this.id = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.status = status;
        this.createdAt = createdAt;
    }
}
