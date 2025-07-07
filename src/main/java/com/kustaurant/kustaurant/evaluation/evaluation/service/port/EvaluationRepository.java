package com.kustaurant.kustaurant.evaluation.evaluation.service.port;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    Integer countAllByStatus(String status);
    List<Evaluation> findByUserId(Long userId);
    List<Evaluation> findSortedEvaluationByUserIdDesc(Long userId);
    Optional<Evaluation> findById(Long id);

}
