package com.kustaurant.kustaurant.global.auth.jwt.core;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.auth.jwt.response.AccessTokenResponse;
import com.kustaurant.kustaurant.global.auth.jwt.response.TokenResponse;
import com.kustaurant.kustaurant.global.auth.jwt.apple.AppleLoginRequest;
import com.kustaurant.kustaurant.global.auth.jwt.naver.NaverLoginRequest;
import com.kustaurant.kustaurant.global.exception.exception.auth.AccessTokenInvalidException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiLoginController {

    private final UserApiLoginService userApiLoginService;
    private final JwtUtil jwtUtil;

    //1
    @Operation(
            summary = "네이버 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 토큰2종 발행해 리턴합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    })
    @PostMapping("/api/v2/login/naver")
    public ResponseEntity<TokenResponse> loginWithNaver(
            @Valid @RequestBody NaverLoginRequest req
    ) {
        return ResponseEntity.ok(userApiLoginService.processNaverLogin(req));
    }


    //2
    @Operation(
            summary = "애플 로그인 api 입니다.",
            description = "기존유저조회or회원가입 처리 후 토큰2종 발행해 리턴합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    })
    @PostMapping("/api/v2/login/apple")
    public ResponseEntity<TokenResponse> loginWithApple(
            @Valid @RequestBody AppleLoginRequest req
    ) {

        return ResponseEntity.ok(userApiLoginService.processAppleLogin(req));
    }


    //3
    @Operation(
            summary = "액세스 토큰 재발행"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발행 성공",
                    content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음"),
            @ApiResponse(responseCode = "400", description = "요청 헤더 누락 또는 잘못된 형식")
    })
    @PostMapping("/api/v2/token/refresh")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        String refresh = stripBearer(refreshToken);
        String newAccessToken = userApiLoginService.refreshAccessToken(refresh);

        return ResponseEntity.ok(new AccessTokenResponse(newAccessToken));
    }


    //4
    @Operation(summary = "발급받은 토큰 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "토큰이 유효합니다"),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않습니다"),
    })
    @GetMapping("/api/v2/token/verify")
    public ResponseEntity<?> verifyToken(
            @RequestHeader("Authorization") String accessToken
    ) {
        String token = stripBearer(accessToken);
        boolean valid = jwtUtil.isValid(token);

        if (!valid) {
            throw new AccessTokenInvalidException();
        }
        return ResponseEntity.noContent().build();
    }


    //5
    @Operation(summary = "로그아웃", description = "서버측 Refresh 제거")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    })
    @PostMapping("/api/v2/auth/logout")
    public ResponseEntity<?> logout(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
            ) {
        userApiLoginService.logoutUser(user.id());
        return ResponseEntity.noContent().build();
    }


    //6
    @Operation(summary = "회원탈퇴", description = "사용자 상태를 DELETED 로 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "탈퇴 완료"),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @DeleteMapping("/api/v2/auth/user")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
    ) {
        userApiLoginService.deleteUserById(user.id());
        return ResponseEntity.noContent().build();
    }

    private String stripBearer(String raw) {
        return (StringUtils.hasText(raw) && raw.toLowerCase().startsWith("bearer "))
                ? raw.substring(7) : raw;
    }


}
