package com.kustaurant.crawler.aianalysis.service;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.AiAnalysisReq;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisJobRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiAnalysisReviewRepo;
import com.kustaurant.crawler.aianalysis.adapter.out.persistence.AiSummaryRepo;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisJob;
import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import com.kustaurant.crawler.aianalysis.domain.service.AiSummaryService;
import jakarta.persistence.OptimisticLockException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeReviewTxService {

    private final AiSummaryService aiSummaryService;

    private final AiAnalysisReviewRepo aiAnalysisReviewRepo;
    private final AiAnalysisJobRepo aiAnalysisJobRepo;
    private final AiSummaryRepo aiSummaryRepo;

    private final Clock clock;

    @Retryable(
            value = {
                    ObjectOptimisticLockingFailureException.class,
                    OptimisticLockException.class,
                    CannotAcquireLockException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(
                    delayExpression = "#{50 + T(java.util.concurrent.ThreadLocalRandom).current().nextInt(150)}",
                    maxDelay = 1500,
                    multiplier = 2.0
            )
    )
    @Transactional
    public void persistResult(AiAnalysisReq req, Optional<AiAnalysisReview> optional) {
        long jobId = req.jobId();
        long restaurantId = req.restaurantId();

        boolean success = optional.isPresent();

        // analyzed review 저장
        if (success) {
            aiAnalysisReviewRepo.save(optional.get());
        } else {
            log.info("failed to analyze review");
        }

        LocalDateTime now = LocalDateTime.now(clock);

        AiAnalysisJob job = aiAnalysisJobRepo.findJob(jobId);
        job.updateReviewCount(success, now);

        // job 이 끝난 경우 식당 분석 Summary 생성
        if (job.complete(now)) {
            AiSummary summary = aiSummaryRepo.findByRestaurantId(restaurantId)
                    .orElse(AiSummary.create(restaurantId, now));
            List<AiAnalysisReview> reviews = aiAnalysisReviewRepo.findAllByJobId(jobId);

            aiSummaryService.summarize(jobId, summary, reviews);

            aiSummaryRepo.save(summary);
        }
        aiAnalysisJobRepo.save(job);
    }
}
