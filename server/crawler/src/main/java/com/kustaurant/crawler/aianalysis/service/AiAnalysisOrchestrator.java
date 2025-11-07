package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.service.AiAnalysisService;
import com.kustaurant.crawler.aianalysis.domain.service.CrawlingService;
import com.kustaurant.crawler.aianalysis.messaging.dto.AiAnalysisRequest;
import com.kustaurant.crawler.aianalysis.service.port.RatingCrawlerRepo;
import com.kustaurant.crawler.global.exception.AiAnalysisException;
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
        List<Review> reviews = crawlingService.crawl(req.url());
        if (reviews.isEmpty()) {
            throw new AiAnalysisException("크롤링된 리뷰가 없습니다. 요청: " + req);
        }
        log.info("식당 ID {} 크롤링 완료. 리뷰 수: {}", req.restaurantId(), reviews.size());
        // AI 전처리
        RestaurantAnalysis result = aiAnalysisService.analyzeReviews(reviews, req.situations());
        log.info("식당 ID {} AI 처리 완료.", req.restaurantId());
        ratingCrawlerRepo.upsertRating(req.restaurantId(), result);
    }
}
