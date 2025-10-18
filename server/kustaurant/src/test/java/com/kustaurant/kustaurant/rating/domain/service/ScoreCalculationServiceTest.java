package com.kustaurant.kustaurant.rating.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import com.kustaurant.kustaurant.mock.common.TestClockHolder;
import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.vo.EvaluationWithContext;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScoreCalculationServiceTest {

    @Mock
    ScorePolicy scorePolicy;
    @Mock
    GlobalStats globalStats;
    LocalDateTime now = LocalDateTime.now();
    ClockHolder clockHolder = new TestClockHolder(now);

    @Test
    @DisplayName("평가 수가 충분히 있고 AI 평가가 있는 경우: "
            + "isTemp는 false / score는 selfScore / normalizedScore는 finalScore")
    void manyEvaluationsAndAiEvaluationExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 3.0;
        double finalScore = 3.5;

        List<EvaluationWithContext> evaluations =
                java.util.Collections.nCopies(10, mock(EvaluationWithContext.class));
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(true);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);
        when(scorePolicy.calculateFinalScore(evaluations.size(), selfScore, aiScore)).thenReturn(finalScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(selfScore);
        assertThat(computed.getNormalizedScore()).isEqualTo(finalScore);
        assertThat(computed.isTemp()).isFalse();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("평가 수가 충분히 있지만 AI 평가가 없는 경우: "
            + "isTemp는 false / score는 selfScore / normalizedScore도 selfScore")
    void manyEvaluationsAndAiEvaluationNotExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 0.0;

        List<EvaluationWithContext> evaluations =
                java.util.Collections.nCopies(10, mock(EvaluationWithContext.class));
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(true);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(selfScore);
        assertThat(computed.getNormalizedScore()).isEqualTo(selfScore);
        assertThat(computed.isTemp()).isFalse();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy, never()).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("평가 수가 기준치보다 적지만 AI 평가가 있는 경우: "
            + "isTemp는 true / score는 0.0 / normalizedScore는 finalScore")
    void littleEvaluationsAndAiEvaluationExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 3.0;
        double finalScore = 3.5;

        List<EvaluationWithContext> evaluations =
                java.util.Collections.nCopies(2, mock(EvaluationWithContext.class));
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(false);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);
        when(scorePolicy.calculateFinalScore(evaluations.size(), selfScore, aiScore)).thenReturn(finalScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(0.0);
        assertThat(computed.getNormalizedScore()).isEqualTo(finalScore);
        assertThat(computed.isTemp()).isTrue();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("평가 수가 0개이고 AI 평가가 있는 경우: "
            + "isTemp는 true / score는 0.0 / normalizedScore는 aiScore")
    void zeroEvaluationsAndAiEvaluationExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 3.0;

        List<EvaluationWithContext> evaluations = List.of();
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(false);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(0.0);
        assertThat(computed.getNormalizedScore()).isEqualTo(aiScore);
        assertThat(computed.isTemp()).isTrue();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy, never()).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("평가 수가 기준치보다 적고 AI 평가가 없는 경우: "
            + "isTemp는 false / score는 0.0 / normalizedScore는 0.0")
    void littleEvaluationsAndAiEvaluationNotExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 0.0;

        List<EvaluationWithContext> evaluations =
                java.util.Collections.nCopies(2, mock(EvaluationWithContext.class));
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(false);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(0.0);
        assertThat(computed.getNormalizedScore()).isEqualTo(0.0);
        assertThat(computed.isTemp()).isFalse();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy, never()).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("평가와 AI 평가가 모두 아예 없는 경우: "
            + "isTemp는 false / score는 0.0 / normalizedScore는 0.0")
    void zeroEvaluationsAndAiEvaluationNotExists() {
        long rid = 3;
        double selfScore = 4.0;
        double aiScore = 0.0;

        List<EvaluationWithContext> evaluations = List.of();
        AiEvaluation aiEval = new AiEvaluation(rid, 0.8, 0.2, 3.0);

        when(scorePolicy.hasEnoughEvaluations(evaluations.size())).thenReturn(false);
        when(scorePolicy.calculateSelfScore(globalStats, evaluations, evaluations.size())).thenReturn(selfScore);
        when(scorePolicy.calculateAiScore(globalStats, aiEval)).thenReturn(aiScore);

        ScoreCalculationService service = new ScoreCalculationService(scorePolicy, clockHolder);
        // when
        Rating computed = service.compute(rid, globalStats, evaluations, aiEval, now);
        // then
        assertThat(computed.getRestaurantId()).isEqualTo(rid);
        assertThat(computed.getScore()).isEqualTo(0.0);
        assertThat(computed.getNormalizedScore()).isEqualTo(0.0);
        assertThat(computed.isTemp()).isFalse();
        assertThat(computed.getRatedAt()).isEqualTo(now);
        verify(scorePolicy, never()).calculateFinalScore(anyInt(), anyDouble(), anyDouble());
    }
}