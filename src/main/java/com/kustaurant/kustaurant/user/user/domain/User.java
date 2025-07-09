package com.kustaurant.kustaurant.user.user.domain;

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
    private final String loginApi;
    private UserStatus status;
    private final String rankImg;
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
            String loginApi,
            UserStatus status,
            String rankImg,
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
        this.rankImg = rankImg;
        this.stats = stats;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User from(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .nickname(entity.getNickname())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .role(entity.getRole())
                .providerId(entity.getProviderId())
                .loginApi(entity.getLoginApi())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .rankImg(entity.getRankImg())
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
        return UserStatus.DELETED.equals(this.status);
    }

    public void delete() {
        this.status   = UserStatus.DELETED;
        this.nickname = new Nickname("(탈퇴한 회원)");
        this.updatedAt = LocalDateTime.now();
    }

    public int getEvalCount() {
        return (stats != null) ? stats.getRatedRestCnt() : 0;
    }
}
