package com.kustaurant.kustaurant.global.auth.jwt.apple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Slf4j
@Service
public class AppleApiService {
    @Value("${apple.APPLE_PUBLIC_KEYS_URL}")
    private String applePublicKeysURL;
    @Value("${apple.APPLE_CLIENT_ID}")
    private String appleClientId;
    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();

    @PostConstruct
    private void init() {
        fetchApplePublicKeys(applePublicKeysURL);
    }

    //1
    //전달된 identity Token을 애플의 공개 키로 검증하고, 토큰이 유효한지 확인
    public Claims verifyAppleIdentityToken(String identityToken) {
        Locator<Key> keyLocator = new ApplePublicKeyLocator();

        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .keyLocator(keyLocator)
                    .requireAudience(appleClientId)
                    .build()
                    .parseSignedClaims(identityToken);

            return claimsJws.getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다.");
        } catch (IncorrectClaimException|MissingClaimException e) {
            throw new RuntimeException("Apple Id 토큰이 유효하지 않습니다.",e);
        } catch (JwtException e) {
            throw new RuntimeException("Apple Id 토큰 검증 실패",e);
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
        } catch (IOException e) {
            log.error("Failed to parse Apple public keys response", e);
            throw new RuntimeException("Failed to fetch Apple public keys due to parsing error", e);
        } catch (RestClientException e) {
            log.error("Failed to fetch Apple public keys from URL: " + publicKeyUrl, e);
            throw new RuntimeException("Failed to fetch Apple public keys due to network error", e);
        } catch (Exception e) {
            log.error("Unexpected error while fetching Apple public keys", e);
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
    private class ApplePublicKeyLocator extends LocatorAdapter<Key> {
        protected Key locate(ProtectedHeader header) {
            return applePublicKeys.get(header.getKeyId());
        }
    }

}
