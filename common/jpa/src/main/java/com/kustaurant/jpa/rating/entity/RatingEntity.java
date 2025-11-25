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
    private double selfScore = 0;

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
    private double finalScore = 0;

    public RatingEntity(long restaurantId, double selfScore, int tier, boolean isTemp,
            LocalDateTime ratedAt, double finalScore
    ) {
        this.restaurantId = restaurantId;
        this.selfScore = selfScore;
        this.tier = tier;
        this.isTemp = isTemp;
        this.ratedAt = ratedAt;
        this.finalScore = finalScore;
    }
}
