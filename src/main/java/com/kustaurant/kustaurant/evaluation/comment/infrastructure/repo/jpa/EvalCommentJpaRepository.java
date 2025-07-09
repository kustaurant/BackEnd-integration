package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvalCommentJpaRepository extends JpaRepository<EvalCommentEntity, Long> {

    Optional<EvalCommentEntity> findByIdAndRestaurantId(Long id, Integer restaurantId);

    List<EvalCommentEntity> findAllByEvaluationIdIn(List<Long> evalIds);
}
