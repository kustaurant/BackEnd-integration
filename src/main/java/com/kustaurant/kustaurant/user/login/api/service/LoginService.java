package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.ProviderApiException;
import com.kustaurant.kustaurant.user.login.api.controller.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
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

    public TokenResponse login(LoginRequest req) {
        LoginProcessor loginProcessor = processors.stream()
                .filter(pr -> pr.supports(req.provider()))
                .findFirst()
                .orElseThrow(() -> new ProviderApiException(ErrorCode.PROVIDER_NOT_VALID));
        User user = loginProcessor.handle(req);
        return tokenService.issue(user);
    }
}
