package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationCommandJpaRepository extends JpaRepository<EvaluationEntity, Long> {

}
