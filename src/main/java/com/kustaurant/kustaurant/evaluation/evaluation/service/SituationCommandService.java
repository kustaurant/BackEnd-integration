package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.EvaluationSituationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.EvaluationSituationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SituationCommandService {

    private final EvaluationSituationRepository evaluationSituationRepository;

    private final RestaurantSituationRelationService restaurantSituationRelationService;

    @Transactional
    public void addSituations(Integer restaurantId, Long evaluationId, List<Long> situationIds) {
        for (Long situationId : situationIds) {
            // Evaluation Situation Item Table
            evaluationSituationRepository.save(new EvaluationSituationEntity(evaluationId, situationId));
            // Restaurant Situation Relation Table
            restaurantSituationRelationService.updateOrCreate(restaurantId, situationId, 1);
        }
    }

    @Transactional
    public void deleteSituationsByEvaluation(Long evaluationId) {
        if (evaluationId != null) {
            evaluationSituationRepository.deleteByEvaluationId(evaluationId);
        }
    }
}
