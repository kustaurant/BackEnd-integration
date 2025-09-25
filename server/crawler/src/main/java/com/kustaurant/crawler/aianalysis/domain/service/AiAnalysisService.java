package com.kustaurant.crawler.aianalysis.domain.service;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.model.ReviewAnalysis;
import com.kustaurant.crawler.aianalysis.domain.service.port.AiProcessor;
import java.time.Duration;
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

    private static final int maxConcurrency = 50;
    private static final Duration perRequestTimeout = Duration.ofSeconds(10);

    private final AiProcessor aiProcessor;

    public RestaurantAnalysis analyzeReviews(List<Review> reviews, List<String> situations) {
        List<ReviewAnalysis> analyses = parallelCall(reviews, situations);

        return RestaurantAnalysis.of(analyses);
    }

    private List<ReviewAnalysis> parallelCall(List<Review> reviews,
            List<String> situations) {
        // 각 리뷰 AI 분석
        try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
            var semaphore = new Semaphore(maxConcurrency);

            List<CompletableFuture<Optional<ReviewAnalysis>>> futures = reviews.stream()
                    .map(review -> CompletableFuture.supplyAsync(() -> {
                                        try {
                                            semaphore.acquire();
                                            // 실제 작업
                                            return aiProcessor.analyzeReview(review, situations);
                                        } catch (InterruptedException e) {
                                            Thread.currentThread().interrupt();
                                            throw new RuntimeException(e);
                                        } finally {
                                            semaphore.release();
                                        }
                                    }, exec)
//                                    .orTimeout(perRequestTimeout.toMillis(), TimeUnit.MILLISECONDS)
                                    // 실패 시 대체값/로그
                                    .exceptionally(ex -> ReviewAnalysis.error(review.body(), ex.getMessage()))
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
}
