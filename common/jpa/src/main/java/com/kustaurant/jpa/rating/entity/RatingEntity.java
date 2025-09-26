package com.kustaurant.jpa.rating.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_rating")
public class RatingEntity {

    @Id
    @Column(name = "restaurant_id")
    private long restaurantId;
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double score;
    @Column(nullable = false, columnDefinition = "INT DEFAULT -1")
    private int tier;
    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isTemp;
    @Column
    private LocalDateTime ratedAt;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiReviewCount;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiPositiveCount;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiNegativeCount;
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private int aiScoreSum;
    @Column(nullable = false, columnDefinition = "DOUBLE DEFAULT 0.0")
    private double aiAvgScore;
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
}
