package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvalCommentLikeRepository extends JpaRepository<EvalCommentLikeEntity, Integer> {

    @Query("select l from EvalCommentLikeEntity l "
            + "where l.userId = :userId and l.restaurantComment.commentId = :commentId")
    Optional<EvalCommentLikeEntity> findByUserIdAndRestaurantCommentId(@Param("userId") Long userId, @Param("commentId") Integer commentId);

    @Query("select l from EvalCommentLikeEntity l "
            + "where l.userId = :userId and l.evaluation.id = :evaluationId")
    Optional<EvalCommentLikeEntity> findByUserIdAndEvaluationId(@Param("userId") Long userId, @Param("evaluationId") Integer evaluationId);
}
