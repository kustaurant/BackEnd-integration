package com.kustaurant.kustaurant.evaluation.comment.controller.port;

import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;

public interface EvalCommCommandService {
    EvalComment create(Long evalCommentId, Integer restaurantId, Long userId, EvalCommentRequest req);
    void delete(Long evalCommentId, Integer restaurantId, Long userId);

}
