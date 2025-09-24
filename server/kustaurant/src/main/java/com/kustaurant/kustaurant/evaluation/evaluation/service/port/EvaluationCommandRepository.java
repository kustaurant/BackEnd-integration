package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;

public interface EvaluationCommandRepository {

    Long create(Evaluation evaluation);

    void reEvaluate(Evaluation evaluation);

    void react(Evaluation evaluation);
}
