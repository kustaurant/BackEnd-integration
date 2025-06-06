package com.kustaurant.kustaurant.global.auth.jwt.core;

import com.kustaurant.kustaurant.global.auth.jwt.customAnno.JwtToken;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.auth.jwt.TokenResponse;
import com.kustaurant.kustaurant.global.auth.jwt.apple.AppleLoginRequest;
import com.kustaurant.kustaurant.global.auth.jwt.naver.NaverLoginRequest;
import com.kustaurant.kustaurant.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
            description = "기존유저조회or회원가입 처리 후 토큰2종 발행해 리턴합니다."
    )
    @PostMapping("/naver-login")
    public ResponseEntity<TokenResponse> loginWithNaver(@RequestBody NaverLoginRequest req) {

        return ResponseEntity.ok(
                userApiLoginService.processNaverLogin(
                        req.getProvider(), req.getProviderId(), req.getNaverAccessToken()
                )
        );
    }

    //2
    @Operation(
            summary = "애플 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 토큰2종 발행해 리턴합니다."
    )
    @PostMapping("/apple-login")
    public ResponseEntity<TokenResponse> loginWithApple(@RequestBody AppleLoginRequest req) {

        return ResponseEntity.ok(
                userApiLoginService.processAppleLogin(
                        req.getProvider(), req.getIdentityToken(), req.getAuthorizationCode()
                )
        );
    }

    //3
    @Operation(
            summary = "액세스 토큰 재발행 API입니다.",
            description = "서버에 있는 리프레시 토큰을 이용해 새로운 액세스토큰을 발급받아 리턴합니다."
    )
    @PostMapping("/new-access-token")
    public ResponseEntity<TokenResponse> refreshAccessToken(
            @RequestHeader("Authorization") String accessToken
    ) {
        String refresh = stripBearer(accessToken);
        String newAccess = userApiLoginService.refreshAccessToken(refresh);

        // refresh 는 그대로, access 만 새로 내려줌
        return ResponseEntity.ok(new TokenResponse(newAccess, refresh));
    }

    //4
    @Operation(
            summary = "발급받은 토큰을 검증하는 API입니다.",
            description = "검증된토큰 : Httpstatus.OK(200), 아니면 HttpStatus.Unauthorized(401) 출력"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰이 유효합니다"),
            @ApiResponse(responseCode = "401", description = "액세스 토큰이 유효하지 않습니다"),
    })
    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String accessToken) {
        boolean valid = jwtUtil.isValid(stripBearer(accessToken));

        return valid
                ? ResponseEntity.ok(new ErrorResponse("OK", "토큰 유효"))
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("UNAUTHORIZED", "토큰 무효"));
    }

    //5
    @Operation(summary = "로그아웃", description = "서버측 Refresh 제거 (Access 는 클라이언트가 폐기)")
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
            @Parameter(hidden = true)
            @JwtToken Integer userId
    ) {
        userApiLoginService.logoutUser(userId);
        return ResponseEntity.ok(new ErrorResponse("OK", "로그아웃이 완료되었습니다."));
    }

    //6
    @Operation(summary = "회원탈퇴", description = "사용자 상태를 DELETED 로 변경")
    @PostMapping("/auth/goodbye-user")
    public ResponseEntity<?> deleteAppleAccount(
            @Parameter(hidden = true)
            @JwtToken Integer userId
    ) {
        boolean isDeleted = userApiLoginService.deleteUserById(userId);

        return isDeleted ? ResponseEntity.ok(new ErrorResponse("OK", "탈퇴 완료"))
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR", "탈퇴 실패"));
    }

    //7
    @Operation(
            summary = "[테스트용] 기간이 만료된 액세스토큰을 받는 API입니다. (expired Access Token)",
            description = "만료기간이 10초짜리인 액세스 토큰을 발급받습니다."
    )
    @PostMapping("/auth/test/expACT") // test with expired access token
    public ResponseEntity<?> testWithExpiredACT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newAccess = userApiLoginService.yoloAccessToken(stripBearer(accessToken));
        return ResponseEntity.ok(new TokenResponse(newAccess, null));
    }

    //8
    @Operation(
            summary = "[테스트용]기간이 만료된 리프레시토큰을 받는 API입니다. (expired Refresh Token)",
            description = "만료기간이 10초짜리인 리프레시 토큰을 발급받습니다."
    )
    @PostMapping("/auth/test/expRFT") // test with expired refresh token
    public ResponseEntity<?> testWithExpiredRFT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newRefresh = userApiLoginService.yoloRefreshToken(stripBearer(accessToken));
        return ResponseEntity.ok(new TokenResponse(null, newRefresh));
    }

    private String stripBearer(String raw) {
        return (StringUtils.hasText(raw) && raw.startsWith("Bearer "))
                ? raw.substring(7) : raw;
    }


}
