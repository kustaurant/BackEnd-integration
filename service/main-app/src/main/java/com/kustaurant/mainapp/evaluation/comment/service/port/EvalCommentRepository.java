package com.kustaurant.mainapp.evaluation.comment.service.port;

import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;

import java.util.List;
import java.util.Optional;

public interface EvalCommentRepository {
    EvalComment save(EvalComment evalComment);
    Optional<EvalComment> findById(Long id);

    Optional<EvalComment> findByIdAndRestaurantId(Long evalCommentId, Long restaurantId);
    List<EvalComment> findAllByEvaluationIdIn(List<Long> evalIds);
}
