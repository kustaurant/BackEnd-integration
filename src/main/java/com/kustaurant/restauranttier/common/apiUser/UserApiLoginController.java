package com.kustaurant.restauranttier.common.apiUser;

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
        String accessToken = user.getAccessToken();

        TokenResponse tokenResponse = new TokenResponse(accessToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(
            summary = "애플 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 액세스토큰을 발행해 리턴합니다."
    )
    @PostMapping("/apple-login")
    public ResponseEntity<TokenResponse> loginWithApple(@RequestBody AppleLoginRequest request) {

        String accessToken = userApiLoginService.processAppleLogin(
                request.getProvider(),
                request.getIdentityToken(),
                request.getAuthorizationCode()
        );
        TokenResponse tokenResponse = new TokenResponse(accessToken);

        return ResponseEntity.ok(tokenResponse);
    }

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

    // 토큰 검증을 위한 테스트용 엔드포인트
    @Operation(
            summary = "발급받은 토큰을 검증하는 테스트용 API입니다.",
            description = "검증된토큰 : valid, 아니면 invalid 출력"
    )
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestParam String token) {
        boolean isValid = jwtUtil.validateToken(token);
        return ResponseEntity.ok(isValid ? "Token is valid" : "Token is invalid");
    }

    @Operation(
            summary = "로그아웃 처리를 위한 API입니다.",
            description = "서버의 토큰정보들을 지웁니다. 클라이언트에서도 액세스 토큰 정보를 지워야합니다."
    )
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "해당 값은 앱 클라이언트에서 실제로 보낼 필요 없는 값입니다. 토큰만 헤더에 넣어서 보내면 자동으로 userId를 꺼내오는 로직으로 되어있습니다.")
            @JwtToken Integer userId
    ) {
        try {
            userApiLoginService.logoutUser(userId);
            return ResponseEntity.ok("로그아웃이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그아웃에 실패했습니다: " + e.getMessage());
        }
    }
}
