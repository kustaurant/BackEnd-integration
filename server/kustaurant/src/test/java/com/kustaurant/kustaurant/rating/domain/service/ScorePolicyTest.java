package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import com.kustaurant.kustaurant.rating.domain.vo.ScorePolicyProp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ScorePolicyTest {

    @Mock
    ScorePolicyProp prop;

    @Test
    @DisplayName("AiEvaluation과 GlobalStats의 값이 하나라도 null일 경우 aiScore는 0.0이 된다.")
    void aiScoreZero() {
        ScorePolicy policy = new ScorePolicy(prop);

        // when
        GlobalStats gs = new GlobalStats(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
        AiEvaluation aiEval = null;
        // then
        Assertions.assertThat(policy.calculateAiScore(gs, aiEval)).isEqualTo(0.0);

        // when
        gs = new GlobalStats(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, null);
        aiEval = new AiEvaluation(3, 1.0, 1.0, 1.0);
        Assertions.assertThat(policy.calculateAiScore(gs, aiEval)).isEqualTo(0.0);
    }
}