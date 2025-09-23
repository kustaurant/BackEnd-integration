package com.kustaurant.mainapp.evaluation.evaluation.service.port;

import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;

public interface EvaluationCommandRepository {

    Long create(Evaluation evaluation);

    void reEvaluate(Evaluation evaluation);

    void react(Evaluation evaluation);
}
