package com.kustaurant.kustaurant.v1.user;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.user.login.api.controller.LoginRequest;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.login.api.service.LoginService;
import com.kustaurant.kustaurant.user.login.api.service.TokenService;
import com.kustaurant.kustaurant.v1.common.ErrorResponse;
import com.kustaurant.kustaurant.v1.user.dto.AppleLoginRequest;
import com.kustaurant.kustaurant.v1.user.dto.NaverLoginRequest;
import com.kustaurant.kustaurant.v1.user.dto.V1TokenResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class loginControllerV1 {

    private final LoginService loginService;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;

    //1
    @PostMapping("/naver-login")
    public ResponseEntity<V1TokenResponse> loginWithNaver(
            @RequestBody NaverLoginRequest request
    ) {
        LoginRequest v2req = new LoginRequest(LoginApi.NAVER, request.getProviderId(), request.getNaverAccessToken(), null);
        TokenResponse v2res = loginService.login(v2req);
        V1TokenResponse v1 = new V1TokenResponse(v2res.accessToken());

        return ResponseEntity.ok(v1);
    }

    //2
    @PostMapping("/apple-login")
    public ResponseEntity<V1TokenResponse> loginWithApple(
            @RequestBody AppleLoginRequest request
    ) {
        LoginRequest v2req = new LoginRequest(LoginApi.APPLE, null, request.getIdentityToken(), request.getAuthorizationCode());
        TokenResponse v2res = loginService.login(v2req);
        V1TokenResponse v1res = new V1TokenResponse(v2res.accessToken());

        return ResponseEntity.ok(v1res);
    }

    //3
    @PostMapping("/new-access-token")
    public ResponseEntity<V1TokenResponse> refreshAccessToken(
            @RequestHeader("Authorization") String accessToken
    ) {
        String refresh = stripBearer(accessToken);
        String newAccessToken = tokenService.refreshAccess(refresh);
        V1TokenResponse v1 = new V1TokenResponse(newAccessToken);

        return ResponseEntity.ok(v1);
    }

    //4
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String accessToken
    ) {

        String token = stripBearer(accessToken);
        boolean isValid = jwtUtil.isValid(token);

        if (isValid) {
            return ResponseEntity.ok(new ErrorResponse("OK", "액세스 토큰이 유효합니다"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("UNAUTHORIZED", "액세스 토큰이 유효하지 않습니다"));
        }
    }

    //5
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @AuthUser AuthUserInfo user
    ) {
        try {
            loginService.logout(user.id());
            return ResponseEntity.ok(new ErrorResponse("OK", "로그아웃이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("UNAUTHORIZED", "로그아웃에 실패했습니다: " + e.getMessage()));
        }
    }

    //6
    @PostMapping("/auth/goodbye-user")
    public ResponseEntity<?> deleteAppleAccount(
            @AuthUser AuthUserInfo user
    ) {
        loginService.withdraw(user.id());

        return ResponseEntity.ok(new ErrorResponse("OK", "회원탈퇴가 성공적으로 이루어졌습니다."));
    }

    //7
    @PostMapping("/auth/test/expACT")
    public ResponseEntity<?> testWithExpiredACT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newAccess = tokenService.yoloAccess(stripBearer(accessToken));
        V1TokenResponse v1 = new V1TokenResponse(newAccess);

        return ResponseEntity.ok(v1);
    }

    //8
    @PostMapping("/auth/test/expRFT") // test with expired refresh token
    public ResponseEntity<?> testWithExpiredRFT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newRefresh = tokenService.yoloRefresh(stripBearer(accessToken));
        V1TokenResponse v1 = new V1TokenResponse(newRefresh);

        return ResponseEntity.ok(v1);
    }

    private String stripBearer(String raw) {
        return (StringUtils.hasText(raw) && raw.toLowerCase().startsWith("bearer ")) ? raw.substring(7) : raw;
    }
}