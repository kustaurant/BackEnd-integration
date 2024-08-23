package com.kustaurant.restauranttier.common.apiUser.core;

import com.kustaurant.restauranttier.common.apiUser.JwtToken;
import com.kustaurant.restauranttier.common.apiUser.JwtUtil;
import com.kustaurant.restauranttier.common.apiUser.TokenResponse;
import com.kustaurant.restauranttier.common.apiUser.apple.AppleLoginRequest;
import com.kustaurant.restauranttier.common.apiUser.naver.NaverLoginRequest;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserApiLoginController {

    private final UserApiLoginService userApiLoginService;
    private final JwtUtil jwtUtil;

    //1
    @Operation(
            summary = "네이버 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 액세스토큰을 발행해 리턴합니다."
    )
    @PostMapping("/naver-login")
    public ResponseEntity<TokenResponse> loginWithNaver(@RequestBody NaverLoginRequest request) {

        User user = userApiLoginService.processNaverLogin(
                request.getProvider(),
                request.getProviderId(),
                request.getNaverAccessToken()
        );
        TokenResponse tokenResponse = new TokenResponse(user.getAccessToken());

        return ResponseEntity.ok(tokenResponse);
    }

    //2
    @Operation(
            summary = "애플 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 액세스토큰을 발행해 리턴합니다."
    )
    @PostMapping("/apple-login")
    public ResponseEntity<TokenResponse> loginWithApple(@RequestBody AppleLoginRequest request) {

        User user = userApiLoginService.processAppleLogin(
                request.getProvider(),
                request.getIdentityToken(),
                request.getAuthorizationCode()
        );
        TokenResponse tokenResponse = new TokenResponse(user.getAccessToken());

        return ResponseEntity.ok(tokenResponse);
    }

    //3
    @Operation(
            summary = "액세스 토큰 재발행 API입니다.",
            description = "서버에 있는 리프레시 토큰을 이용해 새로운 액세스토큰을 발급받아 리턴합니다."
    )
    @PostMapping("/auth/new-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String accessToken) {
        // 리프레시 토큰을 사용해 새로운 액세스 토큰 발급
        String newAccessToken = userApiLoginService.refreshAccessToken(accessToken);

        return ResponseEntity.ok(newAccessToken);
    }

    //4
    @Operation(
            summary = "발급받은 토큰을 검증하는 API입니다.",
            description = "검증된토큰 : Httpstatus.OK, 아니면 HttpStatus.Unauthorized 출력"
    )
    @GetMapping("/auth/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String accessToken) {
        boolean isValid = jwtUtil.validateToken(accessToken);
        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid");
        }
    }

    //5
    @Operation(
            summary = "로그아웃 처리를 위한 API입니다.",
            description = "서버의 토큰정보들을 지웁니다. 클라이언트에서도 액세스 토큰 정보를 지워야합니다."
    )
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @Parameter(hidden = true)
            @JwtToken Integer userId
    ) {
        try {
            userApiLoginService.logoutUser(userId);
            return ResponseEntity.ok("로그아웃이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그아웃에 실패했습니다: " + e.getMessage());
        }
    }

    //6
    @Operation(
            summary = "회원탈퇴를 위한 API입니다.",
            description = "사용자가 애플 계정을 통해 회원탈퇴 요청을 하면, 서버에서 해당 사용자 데이터를 삭제합니다."
    )
    @PostMapping("/auth/goodbye-user")
    public ResponseEntity<?> deleteAppleAccount(
            @Parameter(hidden = true)
            @JwtToken Integer userId
    ) {
        boolean isDeleted = userApiLoginService.deleteUserById(userId);

        if (isDeleted) {
            return ResponseEntity.ok("회원탈퇴가 성공적으로 이루어졌습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 문제로 회원탈퇴에 실패했습니다.");
        }
    }


}
