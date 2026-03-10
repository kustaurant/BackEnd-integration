package com.kustaurant.crawler.aianalysis.adapter.out.persistence.jpa.support;

import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import com.kustaurant.jpa.rating.entity.AiSummaryEntity;

public class AiSummaryMapper {

    public static AiSummary from(AiSummaryEntity entity) {
        return AiSummary.builder()
                .restaurantId(entity.getRestaurantId())
                .lastJobId(entity.getLastJobId())
                .reviewCount(entity.getReviewCount())
                .positiveReviewCount(entity.getPositiveReviewCount())
                .negativeReviewCount(entity.getNegativeReviewCount())
                .totalScoreSum(entity.getTotalScoreSum())
                .avgScore(entity.getAvgScore())
                .lastAnalyzedAt(entity.getLastAnalyzedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static AiSummaryEntity from(AiSummary model) {
        return AiSummaryEntity.builder()
                .restaurantId(model.getRestaurantId())
                .lastJobId(model.getLastJobId())
                .reviewCount(model.getReviewCount())
                .positiveReviewCount(model.getPositiveReviewCount())
                .negativeReviewCount(model.getNegativeReviewCount())
                .totalScoreSum(model.getTotalScoreSum())
                .avgScore(model.getAvgScore())
                .lastAnalyzedAt(model.getLastAnalyzedAt())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }
}
