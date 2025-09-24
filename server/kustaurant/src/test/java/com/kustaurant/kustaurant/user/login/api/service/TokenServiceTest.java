package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.auth.jwt.JwtProperties;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.auth.jwt.TokenType;
import com.kustaurant.kustaurant.global.exception.exception.auth.RefreshTokenInvalidException;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks TokenService tokenService;
    @Mock RefreshTokenStore store;
    @Mock JwtUtil jwt;
    @Mock JwtProperties prop;
    private static final Long   USER_ID = 42L;
    private static final String ROLE    = "ROLE_USER";
    private static final String ACCESS  = "access.jwt";
    private static final String REFRESH = "refresh.jwt";
    private static final Duration RT_TTL = Duration.ofDays(15);
    private static User dummyUser() {
        return User.builder()
                .id(USER_ID)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }
    private static JwtUtil.ParsedToken parsed(TokenType type) {
        return new JwtUtil.ParsedToken(USER_ID, ROLE, type);
    }


    @Test @DisplayName("issue() - 로그인 직후 토큰 2종 발급 및 Refresh 저장")
    void issue_returnsTokens_andSavesRefresh() {
        // g
        User user = dummyUser();
        when(jwt.generateAccess(USER_ID, ROLE)).thenReturn(ACCESS);
        when(jwt.generateRefresh(USER_ID, ROLE)).thenReturn(REFRESH);
        when(prop.getRefreshTtl()).thenReturn(RT_TTL);
        // w
        TokenResponse res = tokenService.issue(user);
        // t
        assertThat(res.accessToken()).isEqualTo(ACCESS);
        assertThat(res.refreshToken()).isEqualTo(REFRESH);
        verify(store).save(USER_ID, REFRESH, RT_TTL);
    }

    @Nested
    @DisplayName("토큰 재발행")
    class RefreshAccess {
        @Test @DisplayName("유효한 Refresh 토큰이면 새 Access 토큰 반환")
        void returnsNewAccess_whenRefreshValid() {
            // g
            when(jwt.parse(REFRESH)).thenReturn(parsed(TokenType.REFRESH));
            when(store.get(USER_ID)).thenReturn(REFRESH);
            when(jwt.generateAccess(USER_ID, ROLE)).thenReturn(ACCESS);
            // w
            String newAt = tokenService.refreshAccess(REFRESH);
            // t
            assertThat(newAt).isEqualTo(ACCESS);
        }

        @Test @DisplayName("토큰 타입이 REFRESH가 아니면 예외")
        void throws_whenNotRefreshToken() {
            // g
            when(jwt.parse("wrong")).thenReturn(parsed(TokenType.ACCESS));
            // w,t
            assertThatThrownBy(() -> tokenService.refreshAccess("wrong"))
                    .isInstanceOf(RefreshTokenInvalidException.class);
        }

        @Test @DisplayName("저장돼 있던 Refresh와 다르면 예외")
        void throws_whenRefreshMismatch() {
            // g
            when(jwt.parse(REFRESH)).thenReturn(parsed(TokenType.REFRESH));
            when(store.get(USER_ID)).thenReturn("other-refresh");
            // t
            assertThatThrownBy(() -> tokenService.refreshAccess(REFRESH))
                    .isInstanceOf(RefreshTokenInvalidException.class);
        }
    }

    @Test @DisplayName("yoloAccess()는 파싱 후 YOLO Access 발급")
    void yoloAccess_returnsYoloAccess() {
        //g
        when(jwt.parse(ACCESS)).thenReturn(parsed(TokenType.ACCESS));
        when(jwt.generateYOLO(USER_ID, ROLE)).thenReturn("yolo-at");
        //w
        String yolo = tokenService.yoloAccess(ACCESS);
        //t
        assertThat(yolo).isEqualTo("yolo-at");
    }

    @Test
    @DisplayName("yoloRefresh()는 YOLO Refresh 발급 및 저장")
    void yoloRefresh_returnsYoloRefresh_andSaves() {
        //g
        when(jwt.parse(ACCESS)).thenReturn(parsed(TokenType.ACCESS));
        when(jwt.generateYOLO(USER_ID, ROLE)).thenReturn("yolo-rt");
        //w
        String yoloRt = tokenService.yoloRefresh(ACCESS);
        //t
        assertThat(yoloRt).isEqualTo("yolo-rt");
        verify(store).save(USER_ID, "yolo-rt", Duration.ofSeconds(10));
    }
}