package com.kustaurant.restauranttier.common.apiUser.apple;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AppleApiService {

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";
    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();

    public AppleApiService() {
        fetchApplePublicKeys(APPLE_PUBLIC_KEYS_URL);
    }

    public Claims verifyAppleIdentityToken(String identityToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKeyResolver(new AppleSigningKeyResolver(applePublicKeys))
                    .build()
                    .parseClaimsJws(identityToken);

            return claimsJws.getBody();
        } catch (Exception e) {
            log.error("Failed to verify Apple identity token", e);
            throw new RuntimeException("Invalid Apple identity token");
        }
    }

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

    private PublicKey generatePublicKey(String modulus, String exponent) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
        byte[] eBytes = Base64.getUrlDecoder().decode(exponent);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(new java.math.BigInteger(1, nBytes), new java.math.BigInteger(1, eBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    private static class AppleSigningKeyResolver extends SigningKeyResolverAdapter {
        private final Map<String, PublicKey> applePublicKeys;

        public AppleSigningKeyResolver(Map<String, PublicKey> applePublicKeys) {
            this.applePublicKeys = applePublicKeys;
        }

        @Override
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            return applePublicKeys.get(header.getKeyId());
        }
    }
}
