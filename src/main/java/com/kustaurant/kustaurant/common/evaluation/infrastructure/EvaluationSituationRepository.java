package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationSituationRepository extends JpaRepository<EvaluationSituationEntity, EvaluationSituationId> {
    void deleteByEvaluation(EvaluationEntity evaluation);
}
