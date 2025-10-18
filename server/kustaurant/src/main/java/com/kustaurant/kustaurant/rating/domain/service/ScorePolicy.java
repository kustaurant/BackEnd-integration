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
        final double TAU_SENT = 2.0;
        final double TAU_RATE = 2.0;
        final double L0 = 0.2, L1 = 0.6; // w_sent(c) = clip(L0 + L1*c, 0.2~0.8)

        // 전역 통계
        Double meanSelf = gs.meanSelf(), stdSelf = gs.stdSelf();
        Double meanAi   = gs.meanAi(),   stdAi   = gs.stdAi();
        Double meanPos  = gs.meanPos(),  stdPos  = gs.stdPos();
        Double meanNeg  = gs.meanNeg(),  stdNeg  = gs.stdNeg();

        // 1) 감성 신호 u_sent ∈ (0,1)
        Double uSent = null;
        if (meanPos != null && stdPos != null && stdPos > 0
                && meanNeg != null && stdNeg != null && stdNeg > 0) {
            double zp = (aiEval.positiveRatio() - meanPos) / stdPos;
            double zq = (aiEval.negativeRatio() - meanNeg) / stdNeg;
            double s  = zp - zq;
            uSent = 0.5 * (Math.tanh(s / TAU_SENT) + 1.0);
        }

        // 2) AI 별점 신호 u_rate ∈ (0,1)  (AI 분포 → 내부 분포로 보정 후 표준화)
        Double uRate = null;
        if (meanAi != null && stdAi != null && stdAi > 0
                && meanSelf != null && stdSelf != null && stdSelf > 0) {
            double zAi  = (aiEval.aiScoreAvg() - meanAi) / stdAi;
            double rCal = meanSelf + stdSelf * zAi;                  // 내부 분포로 정렬
            double zIn  = (rCal - meanSelf) / stdSelf;               // 내부 분포 기준 z
            uRate = 0.5 * (Math.tanh(zIn / TAU_RATE) + 1.0);
        }

        // 결측 처리
        if (uSent == null && uRate == null) return 0.0;
        if (uSent == null) return 5.0 * clamp01(uRate);
        if (uRate == null) return 5.0 * clamp01(uSent);

        // 3) coverage로 감성 비중 조절
        double coverage = clamp01(aiEval.getCoverage());
        double wSent = clamp(L0 + L1 * coverage, 0.2, 0.8);

        // 4) 결합 → 5점제
        double uAi = wSent * uSent + (1.0 - wSent) * uRate;
        return 5.0 * clamp01(uAi);
    }

    private static double clamp01(Double v) {
        double x = (v == null) ? 0.0 : v;
        return Math.max(0.0, Math.min(1.0, x));
    }
    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
