//package com.kustaurant.restauranttier.common.apiUser.apple;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwsHeader;
//import io.jsonwebtoken.SigningKeyResolverAdapter;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.security.Key;
//import java.security.KeyFactory;
//import java.security.PublicKey;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//public class AppleSigningKeyResolver extends SigningKeyResolverAdapter {
//
//    private final Map<String, PublicKey> applePublicKeys = new HashMap<>();
//
//    public AppleSigningKeyResolver(String publicKeyUrl) {
//        // Apple의 공개 키를 가져와서 캐싱
//        fetchApplePublicKeys(publicKeyUrl);
//    }
//
//    @Override
//    public Key resolveSigningKey(JwsHeader header, Claims claims) {
//        return applePublicKeys.get(header.getKeyId());
//    }
//
//    private void fetchApplePublicKeys(String publicKeyUrl) {
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//            ResponseEntity<String> response = restTemplate.getForEntity(publicKeyUrl, String.class);
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode keys = objectMapper.readTree(response.getBody()).get("keys");
//
//            for (JsonNode key : keys) {
//                String keyId = key.get("kid").asText();
//                String modulus = key.get("n").asText();
//                String exponent = key.get("e").asText();
//
//                byte[] nBytes = Base64.getUrlDecoder().decode(modulus);
//                byte[] eBytes = Base64.getUrlDecoder().decode(exponent);
//
//                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(nBytes);
//
//                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//                PublicKey publicKey = keyFactory.generatePublic(keySpec);
//
//                applePublicKeys.put(keyId, publicKey);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to fetch Apple public keys", e);
//        }
//    }
//}
