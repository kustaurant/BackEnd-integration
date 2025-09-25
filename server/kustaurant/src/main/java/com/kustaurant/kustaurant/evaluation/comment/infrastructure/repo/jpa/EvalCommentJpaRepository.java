package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa;

import com.kustaurant.kustaurant.common.enums.Status;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface EvalCommentJpaRepository extends JpaRepository<EvalCommentEntity, Long> {

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<EvalCommentEntity> findById(Long aLong);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<EvalCommentEntity> findByIdAndRestaurantId(Long id, Long restaurantId);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    List<EvalCommentEntity> findAllByEvaluationIdInAndStatus(List<Long> evalIds, Status status);
}
