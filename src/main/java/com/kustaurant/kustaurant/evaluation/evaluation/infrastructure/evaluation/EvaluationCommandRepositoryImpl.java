package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.evaluation;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.dto.EvaluationSaveCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationCommandRepositoryImpl implements EvaluationCommandRepository {

    private final EvaluationCommandJpaRepository jpaRepository;

    @Override
    public Evaluation addEvaluation(EvaluationSaveCondition condition) {
        return null;
    }

    @Override
    public Evaluation updateEvaluation(EvaluationSaveCondition condition) {
        return null;
    }
}
