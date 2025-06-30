package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationSituationRepository extends JpaRepository<EvaluationSituationEntity, EvaluationSituationId> {
    void deleteByEvaluationId(Long evaluationId);
}
