package com.kustaurant.kustaurant.global.auth.jwt.naver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverApiService {
    //액세스 토큰을 사용해 네이버 유저 정보를 불러오는 서비스이다.

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String userInfoUri;

    public JsonNode getUserInfo(String accessToken) {

        String body = webClient.get()
                .uri(userInfoUri)
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .map(msg -> new IllegalStateException("Naver API 실패: " + msg)))
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readTree(body).path("response");
        } catch (IOException e) {
            log.error("Naver JSON 파싱 실패", e);
            throw new IllegalStateException("Naver 응답 파싱 오류", e);
        }
    }
}

