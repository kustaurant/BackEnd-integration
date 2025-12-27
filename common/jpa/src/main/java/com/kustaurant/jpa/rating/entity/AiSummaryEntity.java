package com.kustaurant.jpa.rating.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ai_summary")
public class AiSummaryEntity {

    @Id
    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "last_job_id", nullable = false)
    private Long lastJobId;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "positive_review_count", nullable = false)
    private int positiveReviewCount;

    @Column(name = "negative_review_count", nullable = false)
    private int negativeReviewCount;

    @Column(name = "total_score_sum", nullable = false)
    private double totalScoreSum;

    @Column(name = "avg_score", nullable = false)
    private double avgScore;

    @Column(name = "last_analyzed_at", nullable = false)
    private LocalDateTime lastAnalyzedAt;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
