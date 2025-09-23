package com.kustaurant.dataprocessor.aianalysis;

import com.kustaurant.dataprocessor.aianalysis.domain.model.RestaurantAnalysis;
import com.kustaurant.dataprocessor.aianalysis.domain.model.Review;
import com.kustaurant.dataprocessor.aianalysis.domain.service.AiAnalysisService;
import com.kustaurant.dataprocessor.aianalysis.domain.service.CrawlingService;
import com.kustaurant.dataprocessor.aianalysis.infrastructure.messaging.dto.AiAnalysisRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiAnalysisOrchestrator {

    private final CrawlingService crawlingService;
    private final AiAnalysisService aiAnalysisService;

    public void execute(AiAnalysisRequest req) {
        // 리뷰 크롤링
        List<Review> reviews = crawlingService.crawl(req.url());
        // AI 전처리
        RestaurantAnalysis result = aiAnalysisService.analyzeReviews(reviews, req.situations());
        // DB 저장
    }
}
