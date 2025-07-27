package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.domain.LoginCommand;
import com.kustaurant.kustaurant.user.login.api.provider.LoginProcessor;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final List<LoginProcessor> processors;
    private final TokenService tokenService;

    public TokenResponse login(LoginCommand cmd) {
        LoginProcessor loginProcessor = processors.stream()
                .filter(pr -> pr.supports(cmd.provider()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원 안 하는 Provider"));
        User user = loginProcessor.handle(cmd);
        return tokenService.issue(user);
    }
}
