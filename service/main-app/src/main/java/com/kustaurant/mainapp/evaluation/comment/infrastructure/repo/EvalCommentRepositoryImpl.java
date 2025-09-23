package com.kustaurant.mainapp.evaluation.comment.infrastructure.repo;

import com.kustaurant.mainapp.common.enums.Status;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;
import com.kustaurant.mainapp.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import com.kustaurant.mainapp.evaluation.comment.infrastructure.repo.jpa.EvalCommentJpaRepository;
import com.kustaurant.mainapp.evaluation.comment.service.port.EvalCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EvalCommentRepositoryImpl implements EvalCommentRepository {
    private final EvalCommentJpaRepository jpa;

    @Override
    public EvalComment save(EvalComment evalComment) {
        return jpa.save(EvalCommentEntity.from(evalComment)).toModel();
    }

    @Override
    public Optional<EvalComment> findById(Long id) {
        return jpa.findById(id).map(EvalCommentEntity::toModel);
    }

    @Override
    public Optional<EvalComment> findByIdAndRestaurantId(Long evalCommentId, Long restaurantId) {
        return jpa.findByIdAndRestaurantId(evalCommentId, restaurantId).map(EvalCommentEntity::toModel);
    }

    @Override
    public List<EvalComment> findAllByEvaluationIdIn(List<Long> evalIds) {
        return jpa.findAllByEvaluationIdInAndStatus(evalIds, Status.ACTIVE).stream().map(EvalCommentEntity::toModel).collect(Collectors.toList());
    }

}
