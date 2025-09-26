package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.user.ProviderApiException;
import com.kustaurant.kustaurant.global.exception.exception.user.UserNotFoundException;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
import com.kustaurant.kustaurant.user.login.api.provider.LoginProcessor;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final List<LoginProcessor> processors;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RefreshTokenStore refreshStore;

    //1. 로그인
    public TokenResponse login(LoginRequest req) {
        LoginProcessor loginProcessor = processors.stream()
                .filter(pr -> pr.supports(req.provider()))
                .findFirst()
                .orElseThrow(() -> new ProviderApiException(ErrorCode.PROVIDER_NOT_VALID));
        User user = loginProcessor.handle(req);
        return tokenService.issue(user);
    }

    //2. 로그아웃
    public void logout(Long userId) {
        refreshStore.delete(userId);
    }

    //3. 회원탈퇴 처리
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.softDelete();
        refreshStore.delete(userId);

        log.info("[User 회원탈퇴] id={}, 닉네임={}", userId,user.getNickname().getValue());
    }
}
