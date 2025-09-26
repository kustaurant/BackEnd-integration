package com.kustaurant.kustaurant.user.login.api.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.kustaurant.global.exception.exception.user.ProviderApiException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOAuthClient {
    @Value("${apple.APPLE_PUBLIC_KEYS_URL}") private String applePublicKeysURL;
    @Value("${apple.APPLE_CLIENT_ID}") private String appleClientId;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();

    @PostConstruct
    private void init() {
        fetchApplePublicKeys(applePublicKeysURL);
    }

    /** 1) Identity Token 검증 */
    public Claims verifyAppleIdentityToken(String identityToken) {
        Locator<Key> keyLocator = new ApplePublicKeyLocator();

        try {
            Jws<Claims> jws = Jwts.parser()
                    .keyLocator(keyLocator)
                    .requireAudience(appleClientId)
                    // .requireIssuer("https://appleid.apple.com") // 권장: issuer도 고정
                    .build()
                    .parseSignedClaims(identityToken);

            return jws.getPayload();

        } catch (ExpiredJwtException e) {
            throw new ProviderApiException("APPLE", "토큰이 만료되었습니다.", e);
        } catch (IncorrectClaimException | MissingClaimException e) {
            throw new ProviderApiException("APPLE", "토큰 클레임이 유효하지 않습니다.", e);
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw new ProviderApiException("APPLE", "JWT 형식 오류", e);
        } catch (JwtException e) {
            // kid 미스매치로 실패했을 가능성 → 한번 더 키 새로고침 후 재시도 (선택)
            try {
                fetchApplePublicKeys(applePublicKeysURL);
                Jws<Claims> retried = Jwts.parser()
                        .keyLocator(new ApplePublicKeyLocator())
                        .requireAudience(appleClientId)
                        .build()
                        .parseSignedClaims(identityToken);
                return retried.getPayload();
            } catch (JwtException retry) {
                throw new ProviderApiException("APPLE", "IdentityToken 검증 실패", retry);
            }
        }
    }

    /** 2) Apple 공개키 로드 (RestClient 사용) */
    private void fetchApplePublicKeys(String publicKeyUrl) {
        try {
            String body = restClient.get()
                    .uri(publicKeyUrl)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String err = res.getBody() == null ? "" :
                                new String(res.getBody().readAllBytes());
                        throw new ProviderApiException("APPLE",
                                "공개키 요청 실패: HTTP " + res.getStatusCode() + " : " + err);
                    })
                    .body(String.class);

            JsonNode keys = objectMapper.readTree(body).path("keys");
            if (keys.isMissingNode() || !keys.isArray()) {
                throw new ProviderApiException("APPLE", "공개키 응답 포맷 오류(keys 누락)");
            }

            Map<String, PublicKey> newMap = new HashMap<>();
            for (JsonNode key : keys) {
                String kid = key.path("kid").asText(null);
                String n = key.path("n").asText(null);
                String e = key.path("e").asText(null);
                if (kid == null || n == null || e == null) continue;

                PublicKey pub = generatePublicKey(n, e);
                newMap.put(kid, pub);
            }

            if (newMap.isEmpty()) {
                throw new ProviderApiException("APPLE", "공개키 목록이 비어있습니다.");
            }
            applePublicKeys.clear();
            applePublicKeys.putAll(newMap);

        } catch (ProviderApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch/parse Apple public keys", e);
            throw new ProviderApiException("APPLE", "공개키 갱신 실패", e);
        }
    }

    /** 3) JWK → RSA PublicKey 변환 */
    private PublicKey generatePublicKey(String modulus, String exponent) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] eBytes = Base64.getUrlDecoder().decode(exponent);
        var spec = new RSAPublicKeySpec(new java.math.BigInteger(1, nBytes),
                new java.math.BigInteger(1, eBytes));
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /** 4) JJWT Locator: kid로 공개키 제공 */
    private class ApplePublicKeyLocator extends LocatorAdapter<Key> {
        @Override
        protected Key locate(ProtectedHeader header) {
            return applePublicKeys.get(header.getKeyId());
        }
    }

}
