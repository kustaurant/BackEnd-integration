package com.kustaurant.kustaurant.user.login.api.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.infrastructure.NaverOAuthClient;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverLoginProcessor implements LoginProcessor {
    private final NaverOAuthClient naverClient;
    private final UserRepository userRepo;

    @Override
    public boolean supports(LoginApi type) {
        return type == LoginApi.NAVER;
    }

    @Override
    public User handle(LoginRequest cmd) {
        JsonNode info = naverClient.getUserInfo(cmd.token());
        String email  = info.path("email").asText();
        String pid    = cmd.providerId();

        // 회원 조회 or 신규 생성
        User user = userRepo.findByProviderId(cmd.providerId())
                .orElseGet(() -> userRepo.save(User.createFromNaver(pid, email, Nickname.fromEmail(email))));

        // 탈퇴 상태였다면 재회원가입
        if (user.isDeleted()) {
            user.revive(email, Nickname.fromEmail(email));
        }

        return user;
    }

}
