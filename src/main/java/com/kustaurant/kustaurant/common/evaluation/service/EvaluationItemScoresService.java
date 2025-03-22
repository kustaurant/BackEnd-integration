package com.kustaurant.kustaurant.common.evaluation.service;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.Evaluation;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.EvaluationItemScoreRepository;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationItemScoresService {
    private final EvaluationItemScoreRepository evaluationItemScoreRepository;

    @Transactional
    public void deleteSituationsByEvaluation(EvaluationEntity evaluation) {
        if (evaluation != null) {
            evaluationItemScoreRepository.deleteByEvaluation(evaluation);
        }
    }
}
