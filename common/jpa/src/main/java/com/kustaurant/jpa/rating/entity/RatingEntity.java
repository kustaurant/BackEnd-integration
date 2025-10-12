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
@Table(name = "restaurant_rating")
public class RatingEntity {

    @Id
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double score = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "INT DEFAULT -1")
    private int tier = -1;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isTemp = false;

    @Column
    private LocalDateTime ratedAt;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double normalizedScore = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiReviewCount = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiPositiveCount = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiNegativeCount = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private double aiScoreSum = 0;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double aiAvgScore = 0;

    @Column
    private LocalDateTime aiProcessedAt;

    public RatingEntity(long restaurantId, double score, int tier, boolean isTemp,
            LocalDateTime ratedAt) {
        this.restaurantId = restaurantId;
        this.score = score;
        this.tier = tier;
        this.isTemp = isTemp;
        this.ratedAt = ratedAt;
    }

    public static RatingEntity of(
            long restaurantId, int aiReviewCount, int aiPositiveCount, int aiNegativeCount,
            double aiScoreSum, double aiAvgScore, LocalDateTime aiProcessedAt
    ) {
        return RatingEntity.builder()
                .restaurantId(restaurantId)
                .aiReviewCount(aiReviewCount)
                .aiPositiveCount(aiPositiveCount)
                .aiNegativeCount(aiNegativeCount)
                .aiScoreSum(aiScoreSum)
                .aiAvgScore(aiAvgScore)
                .aiProcessedAt(aiProcessedAt)
                .build();
    }

    public void updateAiData(
            int aiReviewCount, int aiPositiveCount, int aiNegativeCount,
            double aiScoreSum, double aiAvgScore, LocalDateTime aiProcessedAt
    ) {
        this.aiReviewCount = aiReviewCount;
        this.aiPositiveCount = aiPositiveCount;
        this.aiNegativeCount = aiNegativeCount;
        this.aiScoreSum = aiScoreSum;
        this.aiAvgScore = aiAvgScore;
        this.aiProcessedAt = aiProcessedAt;
    }
}
