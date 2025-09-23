package com.kustaurant.mainapp.evaluation.comment.controller.port;

import com.kustaurant.mainapp.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;

public interface EvalCommCommandService {
    EvalComment create(Long evaluationId, Long restaurantId, Long userId, EvalCommentRequest req);
    void delete(Long evalCommentId, Long restaurantId, Long userId);

}
