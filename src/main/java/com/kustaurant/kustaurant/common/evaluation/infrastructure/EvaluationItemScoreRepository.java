package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationItemScoreRepository extends JpaRepository<EvaluationItemScore, EvaluationItemScoreId> {
    void deleteByEvaluation(Evaluation evaluation);
}
