package com.kustaurant.mainapp.user.login.api.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.mainapp.global.exception.exception.user.ProviderApiException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverOAuthClient {
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
                                .flatMap(msg -> Mono.error(new ProviderApiException(
                                        "NAVER", "HTTP " + resp.statusCode() + " : " + msg))))
                .bodyToMono(String.class)
                .block();

        try {
            return objectMapper.readTree(body).path("response");
        } catch (Exception e) {
            throw new ProviderApiException("NAVER", "통신 실패", e);
        }
    }
}

