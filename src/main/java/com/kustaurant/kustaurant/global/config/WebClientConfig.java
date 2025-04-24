package com.kustaurant.kustaurant.global.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {

        // 1) 커넥션 풀
        ConnectionProvider provider = ConnectionProvider.builder("naver-pool")
                .maxConnections(100)                // 동시 커넥션 상한
                .pendingAcquireMaxCount(200)        // 풀 소진될 때 대기 허용
                .pendingAcquireTimeout(Duration.ofSeconds(2))
                .maxIdleTime(Duration.ofSeconds(30))// Idle 커넥션 정리
                .maxLifeTime(Duration.ofMinutes(2)) // TTL
                .evictInBackground(Duration.ofSeconds(20))
                .build();

        // 2) Netty HTTP 클라이언트에 타임아웃 부여
        HttpClient httpClient = HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)   // 3초
                .responseTimeout(Duration.ofSeconds(5));

        // 3) WebClient 빌드
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))  // ← 수정된 부분
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

    }
}
