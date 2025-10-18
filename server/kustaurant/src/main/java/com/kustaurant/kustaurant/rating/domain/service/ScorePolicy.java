package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.AdjustedEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import com.kustaurant.kustaurant.rating.domain.vo.ScorePolicyProp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScorePolicy {

    private final ScorePolicyProp prop;

    public boolean hasEnoughEvaluations(int evaluationCnt) {
        return prop.restaurant().hasEnoughEvaluations(evaluationCnt);
    }

    public double calculateFinalScore(int evalCnt, double selfScore, double aiScore) {
        return prop.restaurant().calculateScore(evalCnt, selfScore, aiScore);
    }

    public double calculateSelfScore(
            GlobalStats globalStats, List<EvaluationWithContext> evaluations, int evaluationCnt
    ) {
        double numerator = prop.priorWeight() * globalStats.meanSelf();
        double weightSum = prop.priorWeight();

        for (EvaluationWithContext evaluation : evaluations) {
            AdjustedEvaluation adj = evaluation.getAdjustedScore(prop.evaluation(), globalStats.meanSelf());
            numerator += adj.adjustedScore() * adj.weight();
            weightSum += adj.weight();
        }

        double weightedMean = numerator / weightSum;
        return prop.restaurant()
                .shrinkByEvaluationCount(weightedMean, globalStats.meanSelf(), evaluationCnt);
    }

    public double calculateAiScore(GlobalStats gs, AiEvaluation aiEval) {
        if (gs == null || !gs.existsAiEvaluation() || aiEval == null) return 0.0;

        // 하이퍼파라미터
        final double L0 = prop.ai().l0(), L1 = prop.ai().l1();

        // 전역 통계. (null 여부를 위에서 검사함)
        double meanSelf = gs.meanSelf(), stdSelf = gs.stdSelf();
        double meanAi   = gs.meanAi(),   stdAi   = gs.stdAi();
        double meanPos  = gs.meanPos(),  stdPos  = gs.stdPos();
        double meanNeg  = gs.meanNeg(),  stdNeg  = gs.stdNeg();

        // 감성 신호 z-score
        double zPos = (stdPos > 0) ? (aiEval.positiveRatio() - meanPos) / stdPos : 0.0;
        double zNeg = (stdNeg > 0) ? (aiEval.negativeRatio() - meanNeg) / stdNeg : 0.0;
        double zSent = (zPos - zNeg) / Math.sqrt(2);

        // 별점 신호 z-score
        double zRate = (stdAi > 0) ? (aiEval.aiScoreAvg() - meanAi) / stdAi : 0.0;

        // coverage 기반 z-score 가중 평균
        double coverage = clamp01(aiEval.getCoverage());
        double wSent = clamp(L0 + L1 * coverage, L0, L0 + L1);
        double zMix = zSent * wSent + zRate * (1 - wSent);

        // 자체 평가 분포에 맞게 점수화
        double aiScore = meanSelf + stdSelf * zMix;
        return clamp(aiScore, 1.0, 5.0);
    }

    private static double clamp01(double v) {
        return clamp(v, 0.0, 1.0);
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
