package com.kustaurant.kustaurant.common.user.domain;

import com.kustaurant.kustaurant.common.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.common.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Integer id;
    private Nickname nickname;
    private PhoneNumber phoneNumber;
    private String email;
    private final UserRole role;
    private final String providerId;
    private final String loginApi;
    private UserStatus status;
    private final String rankImg;
    private final Integer evaluationCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public User(
            Integer id,
            Nickname nickname,
            PhoneNumber phoneNumber,
            String email,
            UserRole role,
            String providerId,
            String loginApi,
            UserStatus status,
            String rankImg,
            Integer evaluationCount,
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
        this.evaluationCount = evaluationCount;
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
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .evaluationCount(entity.getEvaluationList().size())
                .rankImg(entity.getRankImg())
                .build();
    }

    public void revive(String email, Nickname nickname) {
        if (this.status != UserStatus.DELETED) return;

        this.status = UserStatus.ACTIVE;
        this.email = email;
        this.nickname  = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.status == UserStatus.DELETED;
    }

    public void delete() {
        if (this.status == UserStatus.DELETED) {
            throw new IllegalStateException("이미 탈퇴한 회원입니다.");
        }

        this.status   = UserStatus.DELETED;
        this.nickname = new Nickname("(탈퇴한 회원)");
        this.updatedAt = LocalDateTime.now();
    }
}
