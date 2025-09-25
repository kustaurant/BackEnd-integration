package com.kustaurant.kustaurant.evaluation.comment.controller.port;

import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;

public interface EvalCommCommandService {
    EvalComment create(Long evaluationId, Long restaurantId, Long userId, EvalCommentRequest req);
    void delete(Long evalCommentId, Long restaurantId, Long userId);

}
