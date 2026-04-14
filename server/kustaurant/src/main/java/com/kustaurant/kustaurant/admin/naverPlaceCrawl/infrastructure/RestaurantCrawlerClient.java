package com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure;

import com.kustaurant.naverplace.NaverPlaceCrawlRequest;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobResultResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStartResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlJobStatusResponse;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlRequest;
import com.kustaurant.naverplace.sync.NaverPlaceZoneCrawlResult;
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

    public NaverPlaceZoneCrawlResult crawlZone(CrawlScopeType crawlScope) {
        try {
            log.info("request naver place zone crawl. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/test")
                    .bodyValue(new NaverPlaceZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place zone crawl error: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlResult.class)
                    .block(Duration.ofSeconds(180));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }

    public NaverPlaceZoneCrawlResult crawlZoneTest(CrawlScopeType crawlScope) {
        return crawlZone(crawlScope);
    }

    public NaverPlaceZoneCrawlJobStartResponse startZoneCrawlJob(CrawlScopeType crawlScope) {
        try {
            log.info("request naver place zone crawl job start. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/jobs")
                    .bodyValue(new NaverPlaceZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place zone crawl job start error: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobStartResponse.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }

    public NaverPlaceZoneCrawlJobStatusResponse getZoneCrawlJobStatus(String jobId) {
        try {
            return webClient.get()
                    .uri("/api/naver-place/crawl-zone/jobs/{jobId}", jobId)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place zone crawl job status error: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobStatusResponse.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }

    public NaverPlaceZoneCrawlJobResultResponse getZoneCrawlJobResult(String jobId) {
        try {
            return webClient.get()
                    .uri("/api/naver-place/crawl-zone/jobs/{jobId}/result", jobId)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("naver place zone crawl job result error: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobResultResponse.class)
                    .block(Duration.ofSeconds(30));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("crawler server is unavailable. check crawler base url and server status.", e);
        }
    }
}
