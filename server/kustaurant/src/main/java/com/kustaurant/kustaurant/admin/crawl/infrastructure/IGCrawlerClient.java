package com.kustaurant.kustaurant.admin.crawl.infrastructure;

import com.kustaurant.jpa.restaurant.IGPost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IGCrawlerClient {
    private final WebClient webClient;

    public List<IGPost> crawl(String username) {
        return webClient.post()
                .uri("/api/ig/crawl")
                .bodyValue(Map.of("username", username))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        res -> res.bodyToMono(String.class)
                                .map(body -> new IllegalStateException("인스타그램 크롤링 에러: " + body))
                )
                .bodyToFlux(IGPost.class)
                .collectList()
                .block(Duration.ofSeconds(60));
    }
}
