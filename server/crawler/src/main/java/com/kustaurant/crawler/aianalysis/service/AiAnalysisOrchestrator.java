package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.service.AiAnalysisService;
import com.kustaurant.crawler.aianalysis.domain.service.CrawlingService;
import com.kustaurant.crawler.aianalysis.infrastructure.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.aianalysis.service.port.RatingCrawlerRepo;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisOrchestrator {

    private final CrawlingService crawlingService;
    private final AiAnalysisService aiAnalysisService;

    private final RatingCrawlerRepo ratingCrawlerRepo;

    @Transactional
    public void execute(AiAnalysisRequest req) {
        RestaurantAnalysis result = null;
        // 리뷰 크롤링
        try {
            List<Review> reviews = crawlingService.crawl(req.url());
            // AI 전처리
            result = aiAnalysisService.analyzeReviews(reviews, req.situations());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // DB 저장
            if (Objects.isNull(result)) {
                result = RestaurantAnalysis.from(List.of());
            }
            ratingCrawlerRepo.upsertRating(req.restaurantId(), result);
        }
    }
}
