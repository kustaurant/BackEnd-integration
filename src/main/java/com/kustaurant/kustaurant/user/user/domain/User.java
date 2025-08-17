package com.kustaurant.kustaurant.user.user.domain;

import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.web.OAuthAttributes;
import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long id;
    private Nickname nickname;
    private PhoneNumber phoneNumber;
    private String email;
    private final UserRole role;
    private final String providerId;
    private final LoginApi loginApi;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserStats stats;

    @Builder
    public User(
            Long id,
            Nickname nickname,
            PhoneNumber phoneNumber,
            String email,
            UserRole role,
            String providerId,
            LoginApi loginApi,
            UserStatus status,
            UserStats stats,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.status = status;
        this.stats = stats;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User createFromNaver(OAuthAttributes attr) {
        return User.builder()
                .providerId(attr.getProviderId())
                .email(attr.getEmail())
                .nickname(Nickname.fromEmail(attr.getEmail()))
                .role(UserRole.USER)
                .loginApi(attr.getLoginApi())
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static User createFromNaver(String providerId, String email, Nickname nickname) {
        return User.builder()
                .providerId(providerId)
                .loginApi(LoginApi.NAVER)
                .email(email)
                .nickname(nickname)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    public static User createFromApple(String appleId, Nickname nickname) {
        return User.builder()
                .providerId(appleId)
                .loginApi(LoginApi.APPLE)
                .nickname(nickname)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public void changeNickname(Nickname newNickname) {
        this.nickname = newNickname;
    }

    public void changePhoneNumber(PhoneNumber newPhoneNumber) {
        this.phoneNumber = newPhoneNumber;
    }

    public void revive(String email, Nickname nickname) {
        if (this.status != UserStatus.DELETED) return;

        this.status = UserStatus.ACTIVE;
        this.email = email;
        this.nickname  = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.status.equals(UserStatus.DELETED);
    }

    public void softDelete() {
        this.status   = UserStatus.DELETED;
        this.nickname = new Nickname("(탈퇴한 회원)");
        this.updatedAt = LocalDateTime.now();
    }

    public int getEvalCount() {
        return stats.getRatedRestCnt();
    }
}
