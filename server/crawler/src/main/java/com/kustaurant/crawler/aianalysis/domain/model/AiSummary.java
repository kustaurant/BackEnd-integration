package com.kustaurant.crawler.aianalysis.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiSummary {

    private Long restaurantId;
    private Long lastJobId;
    private int reviewCount;
    private int positiveReviewCount;
    private int negativeReviewCount;
    private double totalScoreSum;
    private double avgScore;
    private LocalDateTime lastAnalyzedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AiSummary create(long restaurantId, LocalDateTime now) {
        return AiSummary.builder()
                .restaurantId(restaurantId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(
            long jobId,
            int reviewCount,
            int positiveReviewCount,
            int negativeReviewCount,
            double totalScoreSum,
            double avgScore,
            LocalDateTime now
    ) {
        this.lastJobId = jobId;
        this.reviewCount = reviewCount;
        this.positiveReviewCount = positiveReviewCount;
        this.negativeReviewCount = negativeReviewCount;
        this.totalScoreSum = totalScoreSum;
        this.avgScore = avgScore;
        this.lastAnalyzedAt = now;
        this.updatedAt = now;
    }
}
