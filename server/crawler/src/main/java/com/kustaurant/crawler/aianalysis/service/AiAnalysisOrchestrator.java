package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.service.AiAnalysisService;
import com.kustaurant.crawler.aianalysis.domain.service.CrawlingService;
import com.kustaurant.crawler.aianalysis.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.aianalysis.service.port.RatingCrawlerRepo;
import java.util.List;
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
        // 리뷰 크롤링
        try {
            List<Review> reviews = crawlingService.crawl(req.url());
            // AI 전처리
            RestaurantAnalysis result = aiAnalysisService.analyzeReviews(reviews, req.situations());
            ratingCrawlerRepo.upsertRating(req.restaurantId(), result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
