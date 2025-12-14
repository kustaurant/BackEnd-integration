package com.kustaurant.crawler.aianalysis.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiAnalysisJob {

    private static final int MIN_REVIEW_COUNT = 10;

    private Long id;
    private Long restaurantId;
    private JobStatus status;
    private int totalReviews;
    private int processedReviews;
    private int failedReviews;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AiAnalysisJob create(Long restaurantId, LocalDateTime now) {
        return AiAnalysisJob.builder()
                .restaurantId(restaurantId)
                .status(JobStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void startJob(int totalReviews, LocalDateTime now) {
        if (status != JobStatus.PENDING)
            throw new IllegalStateException(
                    String.format("Job is not pending. Job Id: %d, Status: %s", id, status));
        if (totalReviews <= MIN_REVIEW_COUNT)
            throw new IllegalArgumentException(
                    String.format("Total reviews must be greater than %d. current reviews: %d",
                            MIN_REVIEW_COUNT, totalReviews));
        this.totalReviews = totalReviews;
        startedAt = now;
        updatedAt = now;
        status = JobStatus.RUNNING;
    }

    public void doneJob(LocalDateTime now) {
        status = JobStatus.DONE;
        completedAt = now;
        updatedAt = now;
    }

    public void failJob(String failMsg, LocalDateTime now) {
        errorMessage = failMsg;
        status = JobStatus.FAILED;
        updatedAt = now;
    }

    public boolean complete(LocalDateTime now) {
        // 아직 모든 리뷰가 처리되지 않음
        if (this.totalReviews > this.processedReviews + this.failedReviews) {
            return false;
        }

        boolean isComplete = this.processedReviews * 1.0 / this.totalReviews >= 0.8;

        if (isComplete)
            doneJob(now);
        else
            failJob("The proportion of successfully analyzed reviews is below the threshold.", now);

        return isComplete;
    }
}
