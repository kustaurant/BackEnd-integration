package com.kustaurant.crawler.aianalysis.domain.service;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.model.ReviewAnalysis;
import com.kustaurant.crawler.aianalysis.domain.service.port.AiProcessor;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisService {

    private static final int MAX_CONCURRENCY = 10;
    private static final int MAX_REVIEWS = 100;
    private static final Duration perRequestTimeout = Duration.ofSeconds(10);
    private static final Semaphore semaphore = new Semaphore(MAX_CONCURRENCY);

    private final AiProcessor aiProcessor;

    public RestaurantAnalysis analyzeReviews(List<Review> reviews, List<String> situations) {
        List<ReviewAnalysis> analyses = parallelCall(reviews, situations);

        return RestaurantAnalysis.from(analyses);
    }

    private List<ReviewAnalysis> parallelCall(List<Review> reviews,
            List<String> situations) {
        reviews = reviews.subList(0, Math.min(reviews.size(), MAX_REVIEWS));
        // 각 리뷰 AI 분석
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Optional<ReviewAnalysis>>> futures = reviews.stream()
                    .map(review -> CompletableFuture.supplyAsync(() -> {
                                        try {
                                            semaphore.acquire();
                                            // 실제 작업
                                            return analyzeMultiple(review, situations);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                            throw new RuntimeException(e);
                                        } finally {
                                            log.info("리뷰 하나 처리 완료");
                                            semaphore.release();
                                        }
                                    }, exec)
//                                    .orTimeout(perRequestTimeout.toMillis(), TimeUnit.MILLISECONDS)
                                    // 실패 시 대체값/로그
                                    .exceptionally(ex -> ReviewAnalysis.error(review.body(), ex))
                    )
                    .toList();

            // 모두 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 결과 수집
            return futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(Optional::stream)
                    .toList();
        }
    }

    private Optional<ReviewAnalysis> analyzeMultiple(
            Review review, List<String> situations
    ) {
        List<Optional<ReviewAnalysis>> results = new ArrayList<>();

        // 각 리뷰 별 3회 질의
        results.add(aiProcessor.analyzeReview(review, situations));
        results.add(aiProcessor.analyzeReview(review, situations));
        results.add(aiProcessor.analyzeReview(review, situations));

        return ReviewAnalysis.from(review, results);
    }
}
