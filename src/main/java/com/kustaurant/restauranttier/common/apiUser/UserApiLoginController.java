package com.kustaurant.restauranttier.common.apiUser;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
            description = "프로바이더,식별id,자체발행 액세스토큰을 받아 기존유저조회or회원가입 처리," +
                    "액세스토큰을 발행해 리턴합니다."
    )
    @PostMapping("/naver-login")
    public ResponseEntity<?> loginWithNaver(@RequestParam String provider,
                                            @RequestParam String providerId,
                                            @RequestParam String naverAccessToken) {
        // 로그인 처리 및 JWT 토큰 생성
        User user = userApiLoginService.processNaverLogin(provider, providerId, naverAccessToken);
        String accessToken = user.getAccessToken();

        return ResponseEntity.ok(accessToken);
    }

    @Operation(
            summary = "액세스 토큰 재발행 API입니다.",
            description = "서버에 있는 리프레시 토큰을 이용해 새로운 액세스토큰을 발급받아 리턴합니다."
    )
    @PostMapping("/auth/new-access-token")
    public ResponseEntity<?> refreshAccessToken(@RequestParam String refreshToken) {
        // 리프레시 토큰을 사용해 새로운 액세스 토큰 발급
        String newAccessToken = userApiLoginService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    // 토큰 검증을 위한 테스트용 엔드포인트
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
    public ResponseEntity<?> logout(@RequestParam String refreshToken) {
        try {
            userApiLoginService.logoutUser(refreshToken);
            return ResponseEntity.ok("로그아웃이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그아웃에 실패했습니다: " + e.getMessage());
        }
    }
}
