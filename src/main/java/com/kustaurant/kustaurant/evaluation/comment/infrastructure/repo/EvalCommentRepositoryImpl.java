package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommentJpaRepository;
import com.kustaurant.kustaurant.evaluation.comment.service.port.EvalCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EvalCommentRepositoryImpl implements EvalCommentRepository {
    private final EvalCommentJpaRepository jpaRepository;

    @Override
    public EvalComment save(EvalComment evalComment) {
        return jpaRepository.save(EvalCommentEntity.from(evalComment)).toModel();
    }

    @Override
    public Optional<EvalComment> findById(Long id) {
        return jpaRepository.findById(id).map(EvalCommentEntity::toModel);
    }

    @Override
    public Optional<EvalComment> findByIdAndRestaurantId(Long evalCommentId, Integer restaurantId) {
        return jpaRepository.findByIdAndRestaurantId(evalCommentId, restaurantId).map(EvalCommentEntity::toModel);
    }

    @Override
    public List<EvalComment> findAllByEvaluationIdIn(List<Long> evalIds) {
        return jpaRepository.findAllByEvaluationIdIn(evalIds).stream().map(EvalCommentEntity::toModel).collect(Collectors.toList());
    }

}
