package com.kustaurant.kustaurant.user.login.api.controller;

import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
import com.kustaurant.kustaurant.global.exception.exception.auth.AccessTokenInvalidException;
import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
import com.kustaurant.kustaurant.user.login.api.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "user-token-controller")
public class TokenApiController {
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    //1
    @Operation(summary = "액세스 토큰 재발행")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "액세스 토큰 재발행 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 만료 또는 유효하지 않음"),
            @ApiResponse(responseCode = "400", description = "요청 헤더 누락 또는 잘못된 형식")
    })
    @PostMapping("/v2/token/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        String refresh = stripBearer(refreshToken);
        String newAccessToken = tokenService.refreshAccess(refresh);

        return ResponseEntity.ok(new TokenResponse(newAccessToken, null));
    }


    //2
    @Operation(summary = "발급받은 토큰 검증")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "토큰이 유효합니다"),
            @ApiResponse(responseCode = "401", description = "토큰이 유효하지 않습니다"),
    })
    @GetMapping("/v2/token/verify")
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

    private String stripBearer(String raw) {
        return (StringUtils.hasText(raw) && raw.toLowerCase().startsWith("bearer "))
                ? raw.substring(7) : raw;
    }
}
