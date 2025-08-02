package com.kustaurant.kustaurant.user.login.api.provider;

import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.infrastructure.AppleOAuthClient;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppleLoginProcessor implements LoginProcessor {
    private final AppleOAuthClient appleClient;
    private final UserRepository userRepo;

    @Override
    public boolean supports(LoginApi p) {
        return p == LoginApi.APPLE;
    }

    @Override
    public User handle(LoginRequest cmd) {
        Claims claims = appleClient.verifyAppleIdentityToken(cmd.token());
        String appleId = claims.getSubject();

        // 회원 조회 or 신규 생성
        User user = userRepo.findByProviderId(appleId)
                .orElseGet(() -> {
                    Nickname nick = new Nickname(nextAppleNickname());
                    return userRepo.save(User.createFromApple(appleId, nick)); // 도메인 정적 팩토리 직접 호출
                });

        // 탈퇴 상태였다면 재회원가입
        if (user.isDeleted()) {
            user.revive(null, new Nickname(nextAppleNickname()));        // 도메인 메서드 직접 호출
        }
        return user;
    }

    private String nextAppleNickname() {
        int idx = userRepo.countByLoginApi(LoginApi.APPLE) + 1;
        return "애플사용자" + idx;
    }
}
