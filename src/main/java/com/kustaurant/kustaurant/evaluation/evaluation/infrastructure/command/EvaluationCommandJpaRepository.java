package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.command;

import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.EvaluationEntity;
import org.springframework.data.repository.Repository;

public interface EvaluationCommandJpaRepository extends Repository<EvaluationEntity, Long> {

}
