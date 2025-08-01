package com.kustaurant.kustaurant.rating.domain.model;

import java.time.LocalDateTime;

public record EvaluationWithContext(
        long evaluationId,
        double score,
        LocalDateTime evaluatedAt,
        boolean existComment,
        boolean existSituation,
        boolean existImage,
        int reactionScore,
        double userAvgScore,
        int userEvalCount
) {

    /**
     * 평가 보정 점수 계산 함수
     * [점수 반영 요소]
     * - 평가의 완성도(상황, 코멘트, 이미지 여부)
     * - 평가 날짜
     * - 평가 좋아요 점수
     * - 유저의 평가 개수, 평균 평가 점수
     */
    public double getAdjustedScore(ScoringPolicy policy) {
        return 0.0;
    }

    // 완성도
    private double calcCompleteness() {
        return 0.6 * bool(existComment) +
                0.2 * bool(existSituation) +
                0.2 * bool(existImage);
    }

    // 최신성
    private double calcRecency() {
        return 0.0;
    }

    private double bool(boolean b) {
        return b ? 1 : 0;
    }
}
