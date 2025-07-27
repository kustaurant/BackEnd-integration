package com.kustaurant.kustaurant.user.login.api.domain;

public record LoginCommand(
        ProviderType provider,   // enum NAVER, APPLE ...
        String providerId,
        String token,      // 네이버: accessToken,  애플: identityToken
        String authCode    // 애플만 쓰고 네이버는 null
) {
}
