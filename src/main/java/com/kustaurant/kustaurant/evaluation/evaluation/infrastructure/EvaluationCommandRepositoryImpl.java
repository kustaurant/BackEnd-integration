package com.kustaurant.kustaurant.evaluation.evaluation.infrastructure;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvaluationCommandJpaRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EvaluationCommandRepositoryImpl implements EvaluationCommandRepository {

    private final EvaluationCommandJpaRepository jpaRepository;

    @Override
    public Long create(Evaluation evaluation) {
        // 저장
        EvaluationEntity saved = jpaRepository.save(EvaluationEntity.from(evaluation));
        // 상황 추가
        saved.updateSituations(evaluation.getSituationIds());

        return saved.getId();
    }

    @Override
    public void reEvaluate(Evaluation evaluation) {
        jpaRepository.findById(evaluation.getId())
                .ifPresent(e -> e.reEvaluate(evaluation)); // 반드시 존재함.
    }
}
