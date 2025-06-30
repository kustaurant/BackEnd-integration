package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.dto.EvaluationSaveCondition;

public interface EvaluationCommandRepository {

    Evaluation addEvaluation(EvaluationSaveCondition condition);

    Evaluation updateEvaluation(EvaluationSaveCondition condition);
}
