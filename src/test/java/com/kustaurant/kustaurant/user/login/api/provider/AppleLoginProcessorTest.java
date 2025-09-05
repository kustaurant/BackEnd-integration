package com.kustaurant.kustaurant.user.login.api.provider;

import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.infrastructure.AppleOAuthClient;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AppleLoginProcessorTest {

    private static final String APPLE_SUB  = "APPLE_SUB_001";
    private static final String ID_TOKEN   = "apple.id.token";
    private static final String AUTH_CODE  = "apple.auth.code";
    @InjectMocks AppleLoginProcessor processor;
    @Mock AppleOAuthClient appleClient;
    @Mock UserRepository userRepo;


    // 공통 Claims stub
    private static Claims claims(String sub) {
        Claims c = mock(Claims.class);
        when(c.getSubject()).thenReturn(sub);
        return c;
    }

    // 공통 LoginRequest 빌더
    private static LoginRequest req() {
        return new LoginRequest(LoginApi.APPLE, null, ID_TOKEN, null);
    }

    @Nested
    @DisplayName("애플 로그인")
    class AppleLogin {
        @Test
        @DisplayName("로그인시 기존 ACTIVE 회원이면 존재하는 유저정보 그대로 반환")
        void existingActiveUser() {
            //g
            LoginRequest req = req();
            Claims parsed = mock(Claims.class);
            when(parsed.getSubject()).thenReturn(APPLE_SUB);
            when(appleClient.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(parsed);

            User existing = User.builder()
                    .id(1L)
                    .providerId(APPLE_SUB)
                    .loginApi(LoginApi.APPLE)
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userRepo.findByProviderId(APPLE_SUB)).thenReturn(Optional.of(existing));

            //w
            User result = processor.handle(req);

            //t
            assertThat(result).isSameAs(existing);
            verify(userRepo, never()).save(any());
        }

        @Test
        @DisplayName("미가입 사용자면 신규 저장 후 반환")
        void newUser() {
            //g
            LoginRequest req = req();
            Claims parsed = mock(Claims.class);
            when(parsed.getSubject()).thenReturn(APPLE_SUB);
            when(appleClient.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(parsed);
            when(userRepo.findByProviderId(APPLE_SUB)).thenReturn(Optional.empty());
            when(userRepo.countByLoginApi(LoginApi.APPLE)).thenReturn(0);

            User saved = User.builder()
                    .id(99L)
                    .providerId(APPLE_SUB)
                    .loginApi(LoginApi.APPLE)
                    .nickname(new Nickname("애플사용자1"))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();
            when(userRepo.save(any(User.class))).thenReturn(saved);

            //w
            User result = processor.handle(req);

            //t
            assertThat(result).isSameAs(saved);
            assertThat(result.getNickname().getValue()).isEqualTo("애플사용자1");
            verify(userRepo).save(any(User.class));
        }

        @Test
        @DisplayName("DELETED 회원이면 revive() 후 ACTIVE로 변경")
        void deletedUserRejoins() {
            // g
            LoginRequest request = req();
            Claims parsed = mock(Claims.class);
            when(parsed.getSubject()).thenReturn(APPLE_SUB);
            when(appleClient.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(parsed);

            User deleted = User.builder()
                    .id(7L)
                    .providerId(APPLE_SUB)
                    .loginApi(LoginApi.APPLE)
                    .nickname(new Nickname("(탈퇴한 회원)"))
                    .status(UserStatus.DELETED)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now().minusDays(30))
                    .build();
            when(userRepo.findByProviderId(APPLE_SUB)).thenReturn(Optional.of(deleted));
            when(userRepo.countByLoginApi(LoginApi.APPLE)).thenReturn(42);

            // w
            User result = processor.handle(request);

            // t
            assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(result.getNickname().getValue()).isEqualTo("애플사용자43");
            verify(userRepo, never()).save(any());
        }
    }

    @Test
    @DisplayName("애플 가입자 42명 존재시, 다음 가입자 닉네임은 애플사용자43")
    void incrementNickname() throws Exception {
        //g
        when(userRepo.countByLoginApi(LoginApi.APPLE)).thenReturn(42);
        //w
        String nick = (String) ReflectionTestUtils.invokeMethod(processor, "nextAppleNickname");
        //t
        assertThat(nick).isEqualTo("애플사용자43");
    }
}