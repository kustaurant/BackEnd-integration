package com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa;

import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationCommandJpaRepository extends JpaRepository<EvaluationEntity, Long> {

}
