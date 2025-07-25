package com.kustaurant.kustaurant.global.auth.jwt.core.test;

import com.kustaurant.kustaurant.global.auth.jwt.core.UserApiLoginService;
import com.kustaurant.kustaurant.global.auth.jwt.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Profile("local")
@RestController("/api/v2/test/token/exp")
@RequiredArgsConstructor
public class TestTokenController {
    private final UserApiLoginService userApiLoginService;

    //1
    @Operation(
            summary = "[테스트용] 만료기간이10초인 액세스토큰을 발행받음"
    )
    @PostMapping("/access")
    public ResponseEntity<?> testWithExpiredACT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newAccess = userApiLoginService.yoloAccessToken(stripBearer(accessToken));
        return ResponseEntity.ok(new TokenResponse(newAccess, null));
    }


    //2
    @Operation(
            summary = "[테스트용] 만료기간이 10초인 리프레시토큰을 발행받음"
    )
    @PostMapping("/refresh") // test expired refresh token
    public ResponseEntity<?> testWithExpiredRFT(
            @RequestHeader("Authorization") String accessToken
    ) {
        String newRefresh = userApiLoginService.yoloRefreshToken(stripBearer(accessToken));
        return ResponseEntity.ok(new TokenResponse(null, newRefresh));
    }

    private String stripBearer(String raw) {
        return (StringUtils.hasText(raw) && raw.toLowerCase().startsWith("bearer "))
                ? raw.substring(7) : raw;
    }
}
