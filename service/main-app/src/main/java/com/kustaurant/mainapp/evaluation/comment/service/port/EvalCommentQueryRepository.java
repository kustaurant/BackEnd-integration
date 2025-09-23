package com.kustaurant.mainapp.evaluation.comment.service.port;

import com.kustaurant.mainapp.evaluation.comment.controller.response.EvalCommentResponse;

public interface EvalCommentQueryRepository {
    EvalCommentResponse fetchEvalCommentWithWriter(Long evalCommentId, Long userId);
}
