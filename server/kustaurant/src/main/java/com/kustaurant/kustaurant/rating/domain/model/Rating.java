package com.kustaurant.kustaurant.rating.domain.model;

import com.kustaurant.kustaurant.rating.domain.vo.Tier;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Rating {

    private long restaurantId;
    private double score;
    private Tier tier;
    private boolean isTemp;
    private LocalDateTime ratedAt;
    private double normalizedScore;

    public Rating(long restaurantId, boolean isTemp, double selfScore, double finalScore, LocalDateTime now) {
        this.restaurantId = restaurantId;
        this.isTemp = isTemp;
        this.score = selfScore;
        this.ratedAt = now;
        this.normalizedScore = finalScore;
    }

    public static Rating combined(long id, double selfScore, double finalScore, LocalDateTime now) {
        return new Rating(id, false, selfScore, finalScore, now);
    }

    public static Rating aiOnly(long id, double finalScore, LocalDateTime now) {
        return new Rating(id, true, 0.0, finalScore, now);
    }

    public static Rating none(long id, LocalDateTime now) {
        return new Rating(id, false, 0.0, 0.0, now);
    }

    public void changeTier(Tier tier) {
        this.tier = tier;
    }
}
