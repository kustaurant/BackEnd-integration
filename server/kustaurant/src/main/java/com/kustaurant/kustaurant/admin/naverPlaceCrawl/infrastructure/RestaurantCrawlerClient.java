package com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure;

import com.kustaurant.naverplace.NaverPlaceCrawlRequest;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

@Slf4j
@Component
public class RestaurantCrawlerClient {

    private final WebClient webClient;

    public RestaurantCrawlerClient(
            WebClient.Builder builder,
            @Value("${crawler.base-url}") String crawlerBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(crawlerBaseUrl)
                .build();
    }

    public NaverPlaceCrawlResult crawlOne(String placeUrl) {
        try {
            log.info("request naver place crawl. placeUrl={}", placeUrl);
            return webClient.post()
                    .uri("/api/naver-place/crawl-one")
                    .bodyValue(new NaverPlaceCrawlRequest(placeUrl))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place crawl error: " + body))
                    )
                    .bodyToMono(NaverPlaceCrawlResult.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }

    public NaverPlaceCrawlResult analyzeOne(String placeUrl) {
        try {
            log.info("request naver place analyze. placeUrl={}", placeUrl);
            return webClient.post()
                    .uri("/api/naver-place/analyze-one")
                    .bodyValue(new NaverPlaceCrawlRequest(placeUrl))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place analyze error: " + body))
                    )
                    .bodyToMono(NaverPlaceCrawlResult.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }
}
