package com.kustaurant.kustaurant.global.auth.apiUser.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverApiService {
    //액세스 토큰을 사용해 네이버 유저 정보를 불러오는 서비스이다.

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String naverUserInfoUrl;

    public JsonNode getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                naverUserInfoUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            return objectMapper.readTree(response.getBody()).path("response");
        } catch (Exception e) {
            log.error("Failed to parse user info from Naver API", e);
            throw new RuntimeException("Naver API로부터 유저정보를 불러오는데 실패했습니다.");
        }
    }
}

