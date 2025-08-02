package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserEntityTest {

    @Test
    @DisplayName("from() 호출 시 stats가 null이면 0으로 초기화된다")
    void fromCreatesDefaultStatsWhenNull() {
        // g
        User domainUser = User.builder()
                .providerId("pid‑n")
                .loginApi(LoginApi.NAVER)
                .email("null@test.com")
                .nickname(new Nickname("nullstats"))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        // w
        UserEntity entity = UserEntity.from(domainUser);

        // t
        assertThat(entity.getStats()).isNotNull();
        assertThat(entity.getStats().getRatedRestCnt()).isZero();
        assertThat(entity.getStats().getCommPostCnt()).isZero();
        assertThat(entity.getStats().getUser()).isSameAs(entity);
    }

    @Test
    @DisplayName("from() 호출 시 stats가 존재하면 값이 그대로 복사된다")
    void fromCopiesStatsValuesWhenPresent() {
        // g
        UserStats stats = UserStats.builder()
                .savedRestCnt(5)
                .ratedRestCnt(3)
                .commPostCnt(2)
                .commCommentCnt(1)
                .commSavedPostCnt(4)
                .build();

        User domainUser = User.builder()
                .providerId("pid‑y")
                .loginApi(LoginApi.APPLE)
                .email("copy@test.com")
                .nickname(new Nickname("copystats"))
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .stats(stats)
                .build();

        // w
        UserEntity entity = UserEntity.from(domainUser);

        // t
        assertThat(entity.getStats()).isNotNull();
        assertThat(entity.getStats().getSavedRestCnt()).isEqualTo(5);
        assertThat(entity.getStats().getRatedRestCnt()).isEqualTo(3);
        assertThat(entity.getStats().getCommPostCnt()).isEqualTo(2);
        assertThat(entity.getStats().getCommCommentCnt()).isEqualTo(1);
        assertThat(entity.getStats().getCommSavedPostCnt()).isEqualTo(4);
        assertThat(entity.getStats().getUser()).isSameAs(entity);
    }

}