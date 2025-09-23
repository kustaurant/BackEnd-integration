package com.kustaurant.mainapp.evaluation.evaluation.infrastructure;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.EvaluationEntity;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa.EvaluationCommandJpaRepository;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
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
        EvaluationEntity entity = jpaRepository.findById(evaluation.getId())
                .orElseThrow(() ->
                        new DataNotFoundException(EVALUATION_NOT_FOUND, evaluation.getId(), "평가"));
        entity.reEvaluate(evaluation);
    }

    @Override
    public void react(Evaluation evaluation) {
        EvaluationEntity entity = jpaRepository.findById(evaluation.getId())
                .orElseThrow(() ->
                        new DataNotFoundException(EVALUATION_NOT_FOUND, evaluation.getId(), "평가"));
        entity.react(evaluation.getLikeCount(), evaluation.getDislikeCount());
    }
}
