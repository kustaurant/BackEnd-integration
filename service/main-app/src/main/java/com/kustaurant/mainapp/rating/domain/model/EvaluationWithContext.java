package com.kustaurant.mainapp.rating.domain.model;

import com.kustaurant.mainapp.rating.domain.model.RatingPolicy.EvaluationPolicy;
import com.kustaurant.mainapp.rating.domain.model.RatingPolicy.EvaluationPolicy.CompletenessWeight;
import com.querydsl.core.annotations.QueryProjection;
import java.time.Duration;
import java.time.LocalDateTime;

public record EvaluationWithContext(
        long restaurantId,
        double score,
        LocalDateTime evaluatedAt,
        boolean existComment,
        boolean existSituation,
        boolean existImage,
        long reactionScore,
        double userAvgScore,
        long userEvalCount
) {

    @QueryProjection
    public EvaluationWithContext(long restaurantId, double score, LocalDateTime evaluatedAt,
            boolean existComment, boolean existSituation, boolean existImage, long reactionScore,
            double userAvgScore, long userEvalCount) {
        this.restaurantId = restaurantId;
        this.score = score;
        this.evaluatedAt = evaluatedAt;
        this.existComment = existComment;
        this.existSituation = existSituation;
        this.existImage = existImage;
        this.reactionScore = reactionScore;
        this.userAvgScore = userAvgScore;
        this.userEvalCount = userEvalCount;
    }

    /**
     * 평가 보정 점수 계산 함수
     * [점수 반영 요소]
     * - 평가의 완성도(상황, 코멘트, 이미지 여부)
     * - 평가 날짜
     * - 평가 좋아요 점수
     * - 유저의 평가 개수, 평균 평가 점수
     */
    public AdjustedEvaluation getAdjustedScore(EvaluationPolicy policy, double globalAvg, LocalDateTime today) {
        // 유저 평가 점수 보정
        double base = getCorrectedScore(globalAvg);

        // 평가의 완성도
        double completeness = getCompleteness(policy);

        // 최근성
        long ageDays = Duration.between(evaluatedAt, today).toDays();
        double recencyBoost = policy.getRecencyBoostW(ageDays);
        double decay = policy.getDecayW(ageDays);

        // 좋아요 지수
        double reaction = policy.getReactionW(reactionScore);

        // 유저의 평가 개수
        double reliability = policy.getEvaluationReliabilityW(userEvalCount);

        double adjustedScore = base * completeness * recencyBoost * reaction;
        double weight = reliability * decay;

        return new AdjustedEvaluation(adjustedScore, weight);
    }

    private double getCorrectedScore(double globalAvg) {
        double corrected = score - userAvgScore + globalAvg;
        corrected = Math.max(1.0, Math.min(5.0, corrected));
        return corrected / 5.0;
    }

    private double getCompleteness(EvaluationPolicy policy) {
        CompletenessWeight evalW = policy.completenessWeight();
        return evalW.score()
                + evalW.comment() * bool(existComment)
                + evalW.situation() * bool(existSituation)
                + evalW.image() * bool(existImage);
    }

    private double bool(boolean b) {
        return b ? 1 : 0;
    }
}
