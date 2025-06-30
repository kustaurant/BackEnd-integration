package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationQueryService {

    private final EvaluationQueryRepository evaluationQueryRepository;

    public boolean isUserEvaluated(Long userId, Integer restaurantId) {
        return evaluationQueryRepository.existsByUserAndRestaurant(userId, restaurantId);
    }

    public boolean hasEvaluation(Integer restaurantId, Integer evaluationId) {
        return evaluationQueryRepository.existsByRestaurantAndEvaluation(restaurantId, evaluationId);
    }

    public EvaluationDTO getPreEvaluation(Long userId, Integer restaurantId) {
        return evaluationQueryRepository.findActiveByUserAndRestaurant(userId, restaurantId)
                .map(EvaluationDTO::toDto)
                .orElse(EvaluationDTO.createIfNotPreEvaluated());
    }
}
