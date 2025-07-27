package com.kustaurant.kustaurant.user.login.api.controller;

import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUser;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.controller.request.AppleLoginRequest;
import com.kustaurant.kustaurant.user.login.api.controller.request.NaverLoginRequest;
import com.kustaurant.kustaurant.user.login.api.domain.LoginCommand;
import com.kustaurant.kustaurant.user.login.api.domain.ProviderType;
import com.kustaurant.kustaurant.user.login.api.service.LoginService;
import com.kustaurant.kustaurant.user.login.api.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApiLoginController {
    private final LoginService facade;
    private final WithdrawService withdrawService;

    //1
    @Operation(
            summary = "네이버 로그인",
            description = "기존유저조회 or 회원가입 처리 후 토큰2종 발행해 리턴합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    })
    @PostMapping("/api/v2/login/naver")
    public ResponseEntity<TokenResponse> loginWithNaver(
            @Valid @RequestBody NaverLoginRequest req
    ) {
        LoginCommand cmd = new LoginCommand(
                ProviderType.NAVER,
                req.providerId(),
                req.naverAccessToken(),
                null
        );

        return ResponseEntity.ok(facade.login(cmd));
    }

    //2
    @Operation(
            summary = "애플 로그인",
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
        LoginCommand cmd = new LoginCommand(
                ProviderType.APPLE,
                null,
                req.identityToken(),
                req.authorizationCode()
        );

        return ResponseEntity.ok(facade.login(cmd));
    }

    //3
    @Operation(summary = "로그아웃", description = "서버측 Refresh 제거")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    })
    @PostMapping("/api/v2/auth/logout")
    public ResponseEntity<?> logout(
            @Parameter(hidden = true) @AuthUser AuthUserInfo user
            ) {
        withdrawService.logoutUser(user.id());
        return ResponseEntity.noContent().build();
    }

    //4
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
        withdrawService.deleteUserById(user.id());
        return ResponseEntity.noContent().build();
    }

}
