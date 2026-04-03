package com.kustaurant.kustaurant.admin.crawl.infrastructure;

import com.kustaurant.jpa.restaurant.IGPost;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.crawl.controller.command.IgCrawlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
public class IGCrawlerClient {
    private final WebClient webClient;

    public IGCrawlerClient(
            WebClient.Builder builder,
            @Value("${crawler.base-url}") String crawlerBaseUrl
    ) {
        this.webClient = builder
                .baseUrl(crawlerBaseUrl)
                .build();
    }

    public List<IGPost> crawl(String accountName, PartnershipTarget target) {
        try{
            log.info("crawler 서버 호출 시작 accountName={}", accountName);
            return webClient.post()
                    .uri("/api/ig/crawl")
                    .bodyValue(new IgCrawlRequest(accountName, target))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("인스타그램 크롤링 에러: " + body))
                    )
                    .bodyToFlux(IGPost.class)
                    .collectList()
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler 서버에 연결할 수 없습니다. 서버 실행 여부와 주소/포트를 확인하세요.", e);
        }

    }
}
