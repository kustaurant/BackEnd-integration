package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.auth.jwt.JwtProperties;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.auth.jwt.TokenType;
import com.kustaurant.kustaurant.global.exception.exception.auth.RefreshTokenInvalidException;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class TokenService {
    private final RefreshTokenStore store;
    private final JwtUtil jwt;
    private final JwtProperties prop;

    // 1. 로그인 직후 발급
    public TokenResponse issue(User u) {
        String at = jwt.generateAccess(u.getId(), u.getRole().getValue());
        String rt = jwt.generateRefresh(u.getId(), u.getRole().getValue());
        store.save(u.getId(), rt, prop.getRefreshTtl());
        return new TokenResponse(at, rt);
    }

    //2. Refresh → 새 AccessToken
    public String refreshAccess(String refreshToken) {
        JwtUtil.ParsedToken tk = jwt.parse(refreshToken);

        if (tk.tokenType() != TokenType.REFRESH)
            throw new RefreshTokenInvalidException();

        String saved = store.get(tk.userId());
        if (!refreshToken.equals(saved))
            throw new RefreshTokenInvalidException();

        return jwt.generateAccess(tk.userId(), tk.role());
    }

    //3. yolo 테스트용
    public String yoloAccess(String anyToken) {
        JwtUtil.ParsedToken t = jwt.parse(anyToken);
        return jwt.generateYOLO(t.userId(), t.role());
    }

    //4. yolo 테스트용
    public String yoloRefresh(String anyToken) {
        JwtUtil.ParsedToken t = jwt.parse(anyToken);
        String rt = jwt.generateYOLO(t.userId(), t.role());
        store.save(t.userId(), rt, Duration.ofSeconds(10));
        return rt;
    }

}
