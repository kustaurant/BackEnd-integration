package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.EvaluationItemScore;
import com.kustaurant.restauranttier.tab3_tier.entity.EvaluationItemScoreId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationItemScoreRepository extends JpaRepository<EvaluationItemScore, EvaluationItemScoreId> {
    void deleteByEvaluation(Evaluation evaluation);
}
