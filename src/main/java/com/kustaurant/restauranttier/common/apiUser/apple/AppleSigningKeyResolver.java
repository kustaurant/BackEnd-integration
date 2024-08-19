package com.kustaurant.restauranttier.common.apiUser.apple;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.security.Key;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class AppleSigningKeyResolver extends SigningKeyResolverAdapter {

    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();

    public AppleSigningKeyResolver(String publicKeyUrl) {
        // Apple의 공개 키를 가져와서 캐싱
        fetchApplePublicKeys(publicKeyUrl);
    }

    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return applePublicKeys.get(header.getKeyId());
    }

    private void fetchApplePublicKeys(String publicKeyUrl) {
        // Apple 공개 키 가져오기 로직 구현 (RestTemplate 사용 가능)
    }
}
