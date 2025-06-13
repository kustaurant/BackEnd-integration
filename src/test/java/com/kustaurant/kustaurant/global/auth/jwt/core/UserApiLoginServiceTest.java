package com.kustaurant.kustaurant.global.auth.jwt.core;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kustaurant.kustaurant.global.auth.jwt.JwtProperties;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.auth.jwt.apple.AppleApiService;
import com.kustaurant.kustaurant.global.auth.jwt.apple.AppleLoginRequest;
import com.kustaurant.kustaurant.global.auth.jwt.naver.NaverApiService;
import com.kustaurant.kustaurant.global.auth.jwt.naver.NaverLoginRequest;
import com.kustaurant.kustaurant.global.auth.jwt.response.TokenResponse;
import com.kustaurant.kustaurant.global.exception.exception.auth.RefreshTokenInvalidException;
import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.domain.enums.UserRole;
import com.kustaurant.kustaurant.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.service.port.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserApiLoginServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    NaverApiService naverApiService;
    @Mock
    AppleApiService appleApiService;
    @Mock RefreshTokenStore   refreshStore;
    @Mock
    JwtUtil jwtUtil;
    @Mock
    JwtProperties jwtProperties;

    @InjectMocks
    private UserApiLoginService service;

    final String ACCESS  = "access.jwt.token";
    final String REFRESH = "refresh.jwt.token";

    @Nested
    @DisplayName("네이버 로그인")
    class NaverLogin {

        @Test
        @DisplayName("기존 ACTIVE회원에게 토큰2종 반환")
        void returnsTokens_whenUserIsActive() {
            //g
            NaverLoginRequest req = new NaverLoginRequest(
                    "NAVER",
                    "PID123",
                    "naverAccess"
            );
            User user = User.builder()
                    .id(1)
                    .nickname(new Nickname("테스트유저"))
                    .email("a@b.com")
                    .providerId("PROVIDER_ID")
                    .loginApi("NAVER")
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();

            ObjectNode naverInfo = JsonNodeFactory.instance.objectNode()
                    .put("email", "a@b.com");

            when(naverApiService.getUserInfo("naverAccess")).thenReturn(naverInfo);
            when(userRepository.findByProviderId("PID123")).thenReturn(Optional.of(user));
            when(jwtUtil.generateAccessToken(1, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(1, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            //w
            var tokenResponse = service.processNaverLogin(req);

            //t
            assertThat(tokenResponse.accessToken()).isEqualTo(ACCESS);
            assertThat(tokenResponse.refreshToken()).isEqualTo(REFRESH);

            verify(refreshStore).save(1, REFRESH, Duration.ofDays(15));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("미가입 사용자라면 signUp 실행해 회원가입 처리후 토큰2종 반환")
        void returnsTokens_whenUserIsNew() {
            // g
            NaverLoginRequest req = new NaverLoginRequest(
                    "NAVER", "NEW_PID", "naverAccess");

            ObjectNode naverInfo = JsonNodeFactory.instance.objectNode()
                    .put("email", "new@user.com");

            when(naverApiService.getUserInfo("naverAccess")).thenReturn(naverInfo);
            when(userRepository.findByProviderId("NEW_PID"))
                    .thenReturn(Optional.empty());

            User newUser = User.builder()
                    .id(99)
                    .nickname(new Nickname("테스트유저"))
                    .email("new@user.com")
                    .providerId("NEW_PID")
                    .loginApi("NAVER")
                    .role(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(newUser);

            when(jwtUtil.generateAccessToken(99, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(99, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            // w
            TokenResponse token = service.processNaverLogin(req);

            //t
            assertThat(token.accessToken()).isEqualTo(ACCESS);
            assertThat(token.refreshToken()).isEqualTo(REFRESH);

            verify(userRepository, times(1)).save(any(User.class));
            verify(refreshStore).save(99, REFRESH, Duration.ofDays(15));
        }

        @Test
        @DisplayName("탈퇴한 사용자가 재로그인시 rejoinIfDeleted 실행해 재가입 처리한뒤 토큰2종 반환()")
        void  returnsTokens_whenUserWasDeleted() {
            // g
            NaverLoginRequest req = new NaverLoginRequest(
                    "NAVER", "PID_DEL", "naverAccess");

            ObjectNode naverInfo = JsonNodeFactory.instance.objectNode()
                    .put("email", "re@join.com");

            User deletedUser = User.builder()
                    .id(7)
                    .providerId("PID_DEL")
                    .loginApi("NAVER")
                    .email("re@join.com")
                    .nickname(new Nickname("삭제됨"))
                    .status(UserStatus.DELETED)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now().minusDays(30))
                    .build();

            when(naverApiService.getUserInfo("naverAccess")).thenReturn(naverInfo);
            when(userRepository.findByProviderId("PID_DEL"))
                    .thenReturn(Optional.of(deletedUser));

            when(jwtUtil.generateAccessToken(7, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(7, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            // w
            TokenResponse token = service.processNaverLogin(req);

            // t
            assertThat(token.accessToken()).isEqualTo(ACCESS);
            assertThat(token.refreshToken()).isEqualTo(REFRESH);
            assertThat(deletedUser.getStatus()).isEqualTo(UserStatus.ACTIVE);

            verify(naverApiService).getUserInfo("naverAccess");
            verify(userRepository).findByProviderId("PID_DEL");
            verify(userRepository, never()).save(any());
            verify(refreshStore).save(7, REFRESH, Duration.ofDays(15));
        }
    }



    @Nested
    @DisplayName("애플 로그인")
    class AppleLogin {

        private static final String APPLE_ID  = "APPLE_SUB_001";
        private static final String ID_TOKEN  = "apple.id.token";
        private static final String AUTH_CODE = "apple.auth.code";

        AppleLoginRequest buildReq() {
            return new AppleLoginRequest("APPLE", ID_TOKEN, AUTH_CODE);
        }

        @Test
        @DisplayName("기존 ACTIVE 회원이면 토큰 2종을 반환한다")
        void returnsTokens_whenUserIsActive() {
            //g
            AppleLoginRequest req = buildReq();
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(APPLE_ID);

            when(appleApiService.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(claims);

            User user = User.builder()
                    .id(1)
                    .providerId(APPLE_ID)
                    .loginApi("APPLE")
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userRepository.findByProviderId(APPLE_ID)).thenReturn(Optional.of(user));
            when(jwtUtil.generateAccessToken(1, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(1, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            //w
            TokenResponse res = service.processAppleLogin(req);

            //t
            assertThat(res.accessToken()).isEqualTo(ACCESS);
            assertThat(res.refreshToken()).isEqualTo(REFRESH);
            verify(refreshStore).save(1, REFRESH, Duration.ofDays(15));
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("미가입 사용자라면 회원가입 처리 후 토큰 2종을 반환한다")
        void returnsTokens_whenUserIsNew() {
            //g
            AppleLoginRequest req = buildReq();
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(APPLE_ID);

            when(appleApiService.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(claims);
            when(userRepository.findByProviderId(APPLE_ID)).thenReturn(Optional.empty());

            User newUser = User.builder()
                    .id(99)
                    .providerId(APPLE_ID)
                    .loginApi("APPLE")
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userRepository.save(any(User.class))).thenReturn(newUser);
            when(jwtUtil.generateAccessToken(99, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(99, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            //w
            TokenResponse res = service.processAppleLogin(req);

            //t
            assertThat(res.accessToken()).isEqualTo(ACCESS);
            assertThat(res.refreshToken()).isEqualTo(REFRESH);
            verify(userRepository).save(any(User.class));
            verify(refreshStore).save(99, REFRESH, Duration.ofDays(15));
        }

        @Test
        @DisplayName("탈퇴 회원이면 재활성화 후 토큰 2종을 반환한다")
        void returnsTokens_whenUserWasDeleted() {
            //g
            AppleLoginRequest req = buildReq();
            Claims claims = mock(Claims.class);
            when(claims.getSubject()).thenReturn(APPLE_ID);

            when(appleApiService.verifyAppleIdentityToken(ID_TOKEN)).thenReturn(claims);

            User deleted = User.builder()
                    .id(7)
                    .providerId(APPLE_ID)
                    .loginApi("APPLE")
                    .status(UserStatus.DELETED)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now().minusDays(60))
                    .build();

            when(userRepository.findByProviderId(APPLE_ID)).thenReturn(Optional.of(deleted));
            when(jwtUtil.generateAccessToken(7, "ROLE_USER")).thenReturn(ACCESS);
            when(jwtUtil.generateRefreshToken(7, "ROLE_USER")).thenReturn(REFRESH);
            when(jwtProperties.getRefreshTtl()).thenReturn(Duration.ofDays(15));

            //w
            TokenResponse res = service.processAppleLogin(req);

            //t
            assertThat(res.accessToken()).isEqualTo(ACCESS);
            assertThat(res.refreshToken()).isEqualTo(REFRESH);
            assertThat(deleted.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(deleted.getNickname().getValue()).isEqualTo("애플사용자1");

            verify(userRepository, never()).save(any());
            verify(refreshStore).save(7, REFRESH, Duration.ofDays(15));
        }
    }



    @Nested
    @DisplayName("Access토큰 재발급")
    class RefreshAccessToken {

        @Test
        @DisplayName("정상 refresh token이면 새로운 access token을 발급한다")
        void refreshAccessToken_success() {
            //g
            int userId = 1;
            String role = "ROLE_USER";
            JwtUtil.ParsedToken parsedToken = new JwtUtil.ParsedToken(userId, role, "RT");

            when(jwtUtil.parse(REFRESH)).thenReturn(parsedToken);
            when(refreshStore.get(userId)).thenReturn(REFRESH);
            when(jwtUtil.generateAccessToken(userId, role)).thenReturn(ACCESS);

            // w
            String newAccessToken = service.refreshAccessToken(REFRESH);

            // then
            assertThat(newAccessToken).isEqualTo(ACCESS);
        }

        @Test
        @DisplayName("저장값과 불일치 -> RefreshTokenInvalidException 발생")
        void refreshAccessToken_mismatch() {
            //g
            JwtUtil.ParsedToken parsed = new JwtUtil.ParsedToken(1, "ROLE_USER", "RT");

            when(jwtUtil.parse(REFRESH)).thenReturn(parsed);
            when(refreshStore.get(1)).thenReturn("DIFFERENT");

            //w + d
            assertThatThrownBy(() -> service.refreshAccessToken(REFRESH))
                    .isInstanceOf(RefreshTokenInvalidException.class);
        }
    }



    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("logoutUser()는 Redis에서 Refresh 토큰을 삭제한다")
        void logoutUser_deletesRefresh() {
            //g
            int userId = 55;

            // when
            service.logoutUser(userId);

            // then
            verify(refreshStore).delete(userId);
            verifyNoInteractions(userRepository);
        }
    }



    @Nested
    @DisplayName("회원탈퇴")
    class DeleteUser {

        @Test
        @DisplayName("deleteUserById()는 status를 DELETED로 바꾸고 Refresh도 삭제한다")
        void deleteUser_marksDeleted() {
            //g
            int userId = 99;
            User testUser = User.builder()
                    .id(userId)
                    .providerId("PID99")
                    .loginApi("NAVER")
                    .email("bye@user.com")
                    .nickname(new Nickname("테스트사용자"))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now().minusDays(10))
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

            // w
            service.deleteUserById(userId);

            // t
            assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
            assertThat(testUser.getNickname().getValue()).isEqualTo("(탈퇴한 회원)");
            verify(refreshStore).delete(userId);
            verify(userRepository, never()).save(any());
        }
    }


    @Nested
    @DisplayName("애플 닉네임 생성")
    class AppleNickname {

        @Test
        @DisplayName("APPLE 가입자가 0명일 때 '애플사용자1'을 반환한다")
        void shouldReturnFirstNickname_whenNoAppleUser() throws Exception {
            // g
            when(userRepository.countByLoginApi("APPLE")).thenReturn(0);

            // w
            String nickname = (String) ReflectionTestUtils
                    .invokeMethod(service, "nextAppleNickname");

            // t
            assertThat(nickname).isEqualTo("애플사용자1");
        }

        @Test
        @DisplayName("APPLE 가입자가 42명일 때 '애플사용자43'을 반환한다")
        void shouldReturnIncrementedNickname_whenSomeAppleUsersExist() throws Exception {
            // g
            when(userRepository.countByLoginApi("APPLE")).thenReturn(42);

            // w
            String nickname = (String) ReflectionTestUtils
                    .invokeMethod(service, "nextAppleNickname");

            // t
            assertThat(nickname).isEqualTo("애플사용자43");
        }
    }
}