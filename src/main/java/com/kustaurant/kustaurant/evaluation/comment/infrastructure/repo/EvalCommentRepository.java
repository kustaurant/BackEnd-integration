package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvalCommentRepository extends JpaRepository<EvalCommentEntity, Long> {

    Optional<EvalCommentEntity> findByCommentId(Integer commentId);

    Optional<EvalCommentEntity> findByCommentIdAndStatus(Integer commentId, String active);

}