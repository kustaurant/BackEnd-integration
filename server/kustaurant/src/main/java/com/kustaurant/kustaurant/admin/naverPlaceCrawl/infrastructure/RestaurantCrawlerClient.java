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
            log.info("네이버 플레이스 단건 크롤 요청. placeUrl={}", placeUrl);
            return webClient.post()
                    .uri("/api/naver-place/crawl-one")
                    .bodyValue(new NaverPlaceCrawlRequest(placeUrl))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버 플레이스 단건 크롤 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceCrawlResult.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public NaverPlaceCrawlResult analyzeOne(String placeUrl) {
        try {
            log.info("네이버 플레이스 단건 분석 요청. placeUrl={}", placeUrl);
            return webClient.post()
                    .uri("/api/naver-place/analyze-one")
                    .bodyValue(new NaverPlaceCrawlRequest(placeUrl))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버 플레이스 단건 분석 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceCrawlResult.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public NaverPlaceZoneCrawlResult crawlZone(CrawlScopeType crawlScope) {
        try {
            log.info("네이버 플레이스 구역 크롤 요청. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/test")
                    .bodyValue(new NaverPlaceZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버 플레이스 구역 크롤 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlResult.class)
                    .block(Duration.ofSeconds(180));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public NaverPlaceZoneCrawlResult crawlZoneTest(CrawlScopeType crawlScope) {
        return crawlZone(crawlScope);
    }

    public NaverPlaceZoneCrawlJobStartResponse startZoneCrawlJob(CrawlScopeType crawlScope) {
        try {
            log.info("네이버 플레이스 구역 크롤 작업 시작 요청. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/jobs")
                    .bodyValue(new NaverPlaceZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버 플레이스 구역 크롤 작업 시작 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobStartResponse.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
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
                                    .map(body -> new IllegalStateException("네이버 플레이스 구역 크롤 작업 상태 조회 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobStatusResponse.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
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
                                    .map(body -> new IllegalStateException("네이버 플레이스 구역 크롤 작업 결과 조회 오류: " + body))
                    )
                    .bodyToMono(NaverPlaceZoneCrawlJobResultResponse.class)
                    .block(Duration.ofSeconds(30));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }
}
