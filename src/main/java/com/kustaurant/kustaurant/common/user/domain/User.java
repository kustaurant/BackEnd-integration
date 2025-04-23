package com.kustaurant.kustaurant.common.user.domain;

import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.webUser.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private Integer id;
    private Nickname nickname;
    private PhoneNumber phoneNumber;
    private String email;
    private UserRole role;
    private String providerId;
    private String loginApi;
    private String accessToken;
    private String refreshToken;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public User(Integer id, Nickname nickname, PhoneNumber phoneNumber, String email, UserRole role, String providerId, String loginApi, String accessToken, String refreshToken, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
        this.updatedAt = updatedAt;
    }

    public void changeNickname(Nickname newNickname) {
        if (this.nickname.equals(newNickname)) {
            throw new IllegalArgumentException("이전과 동일한 닉네임입니다.");
        }
        if (this.updatedAt != null && this.updatedAt.isAfter(LocalDateTime.now().minusDays(30))) {
            throw new IllegalStateException("닉네임 변경은 30일에 한 번만 가능합니다.");
        }
        this.nickname = newNickname;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePhoneNumber(PhoneNumber newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    public static User from(UserEntity entity) {
        return User.builder()
                .id(entity.getUserId())
                .nickname(entity.getNickname())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .role(entity.getRole())
                .providerId(entity.getProviderId())
                .loginApi(entity.getLoginApi())
                .accessToken(entity.getAccessToken())
                .refreshToken(entity.getRefreshToken())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
