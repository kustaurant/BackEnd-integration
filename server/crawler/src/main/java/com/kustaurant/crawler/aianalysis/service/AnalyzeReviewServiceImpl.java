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
public class AnalyzeReviewServiceImpl implements AnalyzeReviewService {

    private final AiReviewService aiReviewService;
    private final AnalyzeReviewTxService analyzeReviewTxService;


    public void analyzeAndSave(AiAnalysisReq req) {

        // AI 분석
        Optional<AiAnalysisReview> optional = aiReviewService.analyzeReview(
                req.jobId(), req.restaurantId(), req.review());
        // 저장
        analyzeReviewTxService.persistResult(req, optional);
    }
}
