package com.kustaurant.kustaurant.evaluation.comment.service.port;

import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;

public interface EvalCommentQueryRepository {
    EvalCommentResponse fetchEvalCommentWithWriter(Long evalCommentId, Long userId);
}
