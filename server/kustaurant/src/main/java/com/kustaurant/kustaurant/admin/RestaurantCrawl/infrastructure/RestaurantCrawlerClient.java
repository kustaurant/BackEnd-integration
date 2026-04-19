package com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure;

import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantCrawlRequest;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.sync.ZoneCrawlJobResultsPayload;
import com.kustaurant.restaurantSync.sync.ZoneCrawlRequest;
import com.kustaurant.restaurantSync.sync.ZoneCrawlResultPayload;
import com.kustaurant.restaurantSync.sync.CrawlJobIdResponse;
import com.kustaurant.restaurantSync.sync.ZoneCrawlStatusPayload;
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
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    public RestaurantRaw crawlOne(String placeId) {
        try {
            log.info("네이버플레이스 단건 크롤 요청. placeId={}", placeId);
            return webClient.post()
                    .uri("/api/naver-place/crawl-one")
                    .bodyValue(new RestaurantCrawlRequest(placeId))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 단건 크롤 오류: " + body))
                    )
                    .bodyToMono(RestaurantRaw.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public RestaurantRaw analyzeOne(String placeId) {
        try {
            log.info("네이버플레이스 단건 분석 요청. placeId={}", placeId);
            return webClient.post()
                    .uri("/api/naver-place/analyze-one")
                    .bodyValue(new RestaurantCrawlRequest(placeId))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 단건 분석 오류: " + body))
                    )
                    .bodyToMono(RestaurantRaw.class)
                    .block(Duration.ofSeconds(60));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public ZoneCrawlResultPayload crawlZone(ZoneType crawlScope) {
        try {
            log.info("네이버플레이스 구역 크롤 요청. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/test")
                    .bodyValue(new ZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 구역 크롤 오류: " + body))
                    )
                    .bodyToMono(ZoneCrawlResultPayload.class)
                    .block(Duration.ofSeconds(180));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public ZoneCrawlResultPayload crawlZoneTest(ZoneType crawlScope) {
        return crawlZone(crawlScope);
    }

    public CrawlJobIdResponse startZoneCrawlJob(ZoneType crawlScope) {
        try {
            log.info("네이버플레이스 구역 크롤 작업 시작 요청. crawlScope={}", crawlScope);
            return webClient.post()
                    .uri("/api/naver-place/crawl-zone/jobs")
                    .bodyValue(new ZoneCrawlRequest(crawlScope))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 구역 크롤 작업 시작 오류: " + body))
                    )
                    .bodyToMono(CrawlJobIdResponse.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public ZoneCrawlStatusPayload getZoneCrawlJobStatus(String jobId) {
        try {
            return webClient.get()
                    .uri("/api/naver-place/crawl-zone/jobs/{jobId}", jobId)
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 구역 크롤 작업 상태 조회 오류: " + body))
                    )
                    .bodyToMono(ZoneCrawlStatusPayload.class)
                    .block(Duration.ofSeconds(20));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }

    public ZoneCrawlJobResultsPayload getZoneCrawlJobResults(String jobId, int fromIndex, int limit) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/naver-place/crawl-zone/jobs/{jobId}/results")
                            .queryParam("fromIndex", fromIndex)
                            .queryParam("limit", limit)
                            .build(jobId))
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            res -> res.bodyToMono(String.class)
                                    .map(body -> new IllegalStateException("네이버플레이스 구역 크롤 작업 결과 조회 오류: " + body))
                    )
                    .bodyToMono(ZoneCrawlJobResultsPayload.class)
                    .block(Duration.ofSeconds(30));
        } catch (WebClientRequestException e) {
            throw new IllegalStateException("크롤러 서버에 연결할 수 없습니다. crawler.base-url과 서버 상태를 확인하세요.", e);
        }
    }
}
