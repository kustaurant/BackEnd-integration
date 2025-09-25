package com.kustaurant.kustaurant.user.login.api.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.kustaurant.global.exception.exception.user.ProviderApiException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverOAuthClient {
    //액세스 토큰을 사용해 네이버 유저 정보를 불러오는 서비스이다.
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String userInfoUri;

    public JsonNode getUserInfo(String accessToken) {

        String body = restClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    String msg = res.getBody() != null ? new String(res.getBody().readAllBytes()) : "";
                    throw new ProviderApiException("NAVER", "HTTP " + res.getStatusCode() + " : " + msg);
                })
                .body(String.class);
        try {
            return objectMapper.readTree(body).path("response");
        } catch (Exception e) {
            throw new ProviderApiException("NAVER", "통신 실패", e);
        }
    }
}

