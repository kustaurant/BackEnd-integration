package com.kustaurant.kustaurant.common.evaluation.service;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.repository.EvaluationItemScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationItemScoresService {
    private final EvaluationItemScoreRepository evaluationItemScoreRepository;

    @Transactional
    public void deleteSituationsByEvaluation(Evaluation evaluation) {
        if (evaluation != null) {
            evaluationItemScoreRepository.deleteByEvaluation(evaluation);
        }
    }
}
