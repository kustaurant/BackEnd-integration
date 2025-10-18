package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreCalculationService {

    private final ScorePolicy policy;
    private final ClockHolder clockHolder;

    public List<Rating> calculateScores(
            List<Long> ids,
            Map<Long, List<EvaluationWithContext>> selfEvalMap,
            Map<Long, AiEvaluation> aiEvalMap,
            GlobalStats globalStats
    ) {
        List<Rating> scores = new ArrayList<>(ids.size());
        LocalDateTime now = clockHolder.now();
        for (long rid : ids) {
            List<EvaluationWithContext> selfEval = selfEvalMap.getOrDefault(rid, Collections.emptyList());
            AiEvaluation aiEval = aiEvalMap.getOrDefault(rid, null);
            Rating score = compute(rid, globalStats, selfEval, aiEval, now);
            scores.add(score);
        }
        return scores;
    }

    /** 각 식당에 대한 점수 계산 */
    public Rating compute(
            long restaurantId,
            GlobalStats globalStats,
            List<EvaluationWithContext> evaluations,
            @Nullable AiEvaluation aiEval,
            LocalDateTime now
    ) {
        int evalCnt = evaluations.size();

        double selfScore = policy.calculateSelfScore(globalStats, evaluations, evalCnt);
        double aiScore = policy.calculateAiScore(globalStats, aiEval);

        boolean enough = policy.hasEnoughEvaluations(evalCnt);
        boolean aiExists = aiScore > 0.0;

        if (enough) {
            double finalScore = aiExists ? policy.calculateFinalScore(evalCnt, selfScore, aiScore) : selfScore;
            return Rating.combined(restaurantId, selfScore, aiScore, finalScore, now);
        }
        if (aiExists) {
            double finalScore = evalCnt > 0 ? policy.calculateFinalScore(evalCnt, selfScore, aiScore) : aiScore;
            return Rating.aiOnly(restaurantId, aiScore, finalScore, now);
        }
        return Rating.none(restaurantId, now);
    }
}
