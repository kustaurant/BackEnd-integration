package com.kustaurant.kustaurant.evaluation.evaluation.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvaluationSituation {

    private Long evaluationId;
    private Situation situation;
}
