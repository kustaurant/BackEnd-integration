package com.kustaurant.restauranttier.common.apiUser.apple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AppleApiService {
    @Value("${APPLE_PUBLIC_KEYS_URL}")
    private String applePublicKeysURL;
    @Value("${APPLE_CLIENT_ID")
    private String appleClientId;
    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();

    public AppleApiService() {
        fetchApplePublicKeys(applePublicKeysURL);
    }

    //1
    //전달된 identity Token을 애플의 공개 키로 검증하고, 토큰이 유효한지 확인
    public Claims verifyAppleIdentityToken(String identityToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKeyResolver(new AppleSigningKeyResolver(applePublicKeys))
                    .build()
                    .parseClaimsJws(identityToken);

            Claims claims = claimsJws.getBody();

            // 토큰 만료 시간 확인
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                throw new RuntimeException("토큰이 만료되었습니다.");
            }

            // Audience 확인 (이 서비스의 클라이언트 ID로 설정)
            String audience = claims.getAudience();
            if (!appleClientId.equals(audience)) {
                throw new RuntimeException("Invalid audience");
            }

            return claims;
        } catch (Exception e) {
            log.error("Failed to verify Apple identity token", e);
            throw new RuntimeException("Invalid Apple identity token");
        }
    }

    //2
    //애플의 공개 키들을 가져와서 초기화하는 메서드
    private void fetchApplePublicKeys(String publicKeyUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(publicKeyUrl, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode keys = objectMapper.readTree(response.getBody()).get("keys");

            for (JsonNode key : keys) {
                String keyId = key.get("kid").asText();
                String modulus = key.get("n").asText();
                String exponent = key.get("e").asText();

                PublicKey publicKey = generatePublicKey(modulus, exponent);
                applePublicKeys.put(keyId, publicKey);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Apple public keys", e);
        }
    }

    //3
    //애플의 공개키를 생성하는 메서드
    private PublicKey generatePublicKey(String modulus, String exponent) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] eBytes = Base64.getUrlDecoder().decode(exponent);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(new java.math.BigInteger(1, nBytes), new java.math.BigInteger(1, eBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    //4
    // JWT 서명 검증 시, 헤더의 키 ID(kid)를 사용하여 해당하는 공개 키를 제공하는 역할
    private static class AppleSigningKeyResolver extends SigningKeyResolverAdapter {
        private final Map<String, PublicKey> applePublicKeys;

        // 생성자, 애플의 공개 키들을 초기화
        public AppleSigningKeyResolver(Map<String, PublicKey> applePublicKeys) {
            this.applePublicKeys = applePublicKeys;
        }

        // 키 ID에 해당하는 공개 키를 반환
        @Override
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            return applePublicKeys.get(header.getKeyId());
        }
    }

}
