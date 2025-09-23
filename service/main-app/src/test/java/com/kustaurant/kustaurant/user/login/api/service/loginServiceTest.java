package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.exception.exception.user.ProviderApiException;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
import com.kustaurant.kustaurant.user.login.api.provider.LoginProcessor;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class loginServiceTest {
    @InjectMocks private LoginService loginService;
    @Mock private TokenService tokenService;
    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenStore refreshStore;
    // Processor 목록은 “네이버만 지원”하는 더미를 하나 만들어 주입
    @Mock LoginProcessor naverProcessor;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(List.of(naverProcessor), tokenService, userRepository, refreshStore);
    }

    @Test
    @DisplayName("지원 Processor가 있으면 issue() 결과 반환")
    void login_ok() {
        // given
        LoginRequest req = new LoginRequest(
                LoginApi.NAVER, "PID", "naverAccess", null);
        User user = User.builder().id(1L).status(UserStatus.ACTIVE).build();

        when(naverProcessor.supports(LoginApi.NAVER)).thenReturn(true);
        when(naverProcessor.handle(req)).thenReturn(user);
        when(tokenService.issue(user)).thenReturn(new TokenResponse("ACCESS", "REFRESH"));

        // when
        TokenResponse res = loginService.login(req);

        // then
        assertThat(res.accessToken()).isEqualTo("ACCESS");
        verify(naverProcessor).handle(req);
        verify(tokenService).issue(user);
    }

    @Test
    @DisplayName("지원 Processor가 없으면 예외")
    void login_wrongProvider() {
        //g
        LoginRequest wrong = new LoginRequest(LoginApi.APPLE, "PID", "appleToken", null);

        //w+t
        assertThatThrownBy(() -> loginService.login(wrong)).isInstanceOf(ProviderApiException.class);
    }

    @Test
    @DisplayName("logoutUser()는 Redis에서 Refresh 토큰을 삭제한다")
    void logoutUser_deletesRefresh() {
        //g
        Long userId = 55L;
        // w
        loginService.logout(userId);
        // t
        verify(refreshStore).delete(userId);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("withdraw()는 status를 DELETED로 바꾸고 Refresh도 삭제한다")
    void withdraw_marksDeleted() {
        //g
        Long userId = 99L;
        User testUser = User.builder()
                .id(userId)
                .providerId("PID99")
                .loginApi(LoginApi.NAVER)
                .email("bye@user.com")
                .nickname(new Nickname("테스트사용자"))
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // w
        loginService.withdraw(userId);

        // t
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(testUser.getNickname().getValue()).isEqualTo("(탈퇴한 회원)");
        verify(refreshStore).delete(userId);
        verify(userRepository, never()).save(any());
    }

}
