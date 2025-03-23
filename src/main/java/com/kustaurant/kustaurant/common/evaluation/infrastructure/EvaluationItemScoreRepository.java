package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.evaluation.infrastructure.evaluation.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationItemScoreRepository extends JpaRepository<EvaluationItemScore, EvaluationItemScoreId> {
    void deleteByEvaluation(EvaluationEntity evaluation);
}
