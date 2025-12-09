package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisJobRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisReviewRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiSummaryRepo;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import com.kustaurant.crawler.aianalysis.domain.service.AiReviewService;
import com.kustaurant.crawler.aianalysis.domain.service.AiSummaryService;
import com.kustaurant.crawler.aianalysis.service.port.AnalyzeReviewService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyzeReviewServiceImpl implements AnalyzeReviewService {

    private final AiReviewService aiReviewService;
    private final AiSummaryService aiSummaryService;

    private final AiAnalysisReviewRepo aiAnalysisReviewRepo;
    private final AiAnalysisJobRepo aiAnalysisJobRepo;
    private final AiSummaryRepo aiSummaryRepo;

    private final Clock clock;

    @Override
    @Transactional
    public void analyzeAndSave(AiAnalysisReq req) {
        long jobId = req.jobId();
        long restaurantId = req.restaurantId();

        // AI 분석
        Optional<AiAnalysisReview> optional = aiReviewService.analyzeReview(
                jobId, restaurantId, req.review());

        boolean success = optional.isPresent();

        // analyzed review 저장
        if (success) {
            aiAnalysisReviewRepo.save(optional.get());
        }

        // review count 업데이트
        aiAnalysisJobRepo.updateReviewCount(jobId, success, LocalDateTime.now(clock));

        // job 이 끝난 경우 식당 분석 Summary 생성
        AiAnalysisJob job = aiAnalysisJobRepo.findJob(jobId);
        if (job.complete(LocalDateTime.now(clock))) {
            AiSummary summary = aiSummaryRepo.findByRestaurantId(restaurantId)
                    .orElse(AiSummary.create(restaurantId, LocalDateTime.now(clock)));
            List<AiAnalysisReview> reviews = aiAnalysisReviewRepo.findAllByJobId(jobId);

            aiSummaryService.summarize(jobId, summary, reviews);

            aiSummaryRepo.save(summary);
        }
        aiAnalysisJobRepo.save(job);
    }
}
