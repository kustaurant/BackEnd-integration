package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.repository.EvaluationItemScoreRepository;
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
