package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Evaluation;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.EvaluationItemScore;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.EvaluationItemScoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationItemScoreRepository extends JpaRepository<EvaluationItemScore, EvaluationItemScoreId> {
    void deleteByEvaluation(Evaluation evaluation);
}
