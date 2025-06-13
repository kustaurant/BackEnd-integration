package com.kustaurant.kustaurant.evaluation.service;

import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.infrastructure.EvaluationSituationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationItemScoresService {
    private final EvaluationSituationRepository evaluationSituationRepository;

    @Transactional
    public void deleteSituationsByEvaluation(EvaluationEntity evaluation) {
        if (evaluation != null) {
            evaluationSituationRepository.deleteByEvaluation(evaluation);
        }
    }
}
