package com.kustaurant.kustaurant.user.login.api.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.infrastructure.NaverOAuthClient;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserRole;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NaverLoginProcessorTest {
    @InjectMocks NaverLoginProcessor processor;

    @Mock NaverOAuthClient naverClient;
    @Mock UserRepository  userRepo;

    private static JsonNode info(String email) {
        return JsonNodeFactory.instance.objectNode().put("email", email);
    }

    @Test
    @DisplayName("로그인시 기존 ACTIVE 유저는 기존 유저정보 그대로 반환")
    void existingActiveUser() {
        //g
        LoginRequest req = new LoginRequest(
                LoginApi.NAVER, "PID123", "naverAccess", null
        );
        User user = User.builder()
                .id(1L)
                .providerId("PID123")
                .loginApi(LoginApi.NAVER)
                .nickname(new Nickname("이미가입한유저"))
                .email("a@b.com")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        when(naverClient.getUserInfo("naverAccess")).thenReturn(info("a@b.com"));
        when(userRepo.findByProviderId("PID123")).thenReturn(Optional.of(user));
        //w
        User result = processor.handle(req);

        //t
        assertThat(result).isSameAs(user);
        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("신규 유저면 createFromNaver -> save 호출")
    void returnsTokens_whenUserIsNew() {
        // g
        LoginRequest req = new LoginRequest(
                LoginApi.NAVER, "NEW_PID", "naverAccess", null
        );

        when(naverClient.getUserInfo("naverAccess")).thenReturn(info("new@user.com"));
        when(userRepo.findByProviderId("NEW_PID")).thenReturn(Optional.empty());

        User newUser = User.builder()
                .id(99L)
                .nickname(new Nickname("테스트유저"))
                .email("new@user.com")
                .providerId("NEW_PID")
                .loginApi(LoginApi.NAVER)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        when(userRepo.save(any(User.class))).thenReturn(newUser);;

        // w
        User result = processor.handle(req);

        //t
        assertThat(result).isEqualTo(newUser);
        verify(userRepo).save(any(User.class));
    }

    @Test
    @DisplayName("DELETED 유저면 revive() 호출 후 ACTIVE로 변경")
    void deletedUserRejoins() {
        // g
        LoginRequest req = new LoginRequest(
                LoginApi.NAVER, "PID_DEL", "naverAccess", null
        );

        User deletedUser = User.builder()
                .id(7L)
                .providerId("PID_DEL")
                .loginApi(LoginApi.NAVER)
                .email("re@join.com")
                .nickname(new Nickname("삭제됨"))
                .status(UserStatus.DELETED)
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now().minusDays(30))
                .build();

        when(naverClient.getUserInfo("naverAccess")).thenReturn(info("re@join.com"));
        when(userRepo.findByProviderId("PID_DEL")).thenReturn(Optional.of(deletedUser));

        // w
        User result = processor.handle(req);

        // t
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVE);
        verify(userRepo, never()).save(any()); // revive() 는 영속 객체 변경이므로 save() 불필요
    }

}