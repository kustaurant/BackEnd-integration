package com.kustaurant.mainapp.evaluation.comment.service;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.evaluation.comment.controller.port.EvalCommCommandService;
import com.kustaurant.mainapp.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.mainapp.evaluation.comment.domain.EvalComment;
import com.kustaurant.mainapp.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.mainapp.evaluation.comment.service.port.EvalCommentRepository;
import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvalCommCommandServiceImpl implements EvalCommCommandService {
    private final EvalCommentRepository evalCommentRepository;
    private final EvalCommUserReactionRepository evalCommUserReactionRepository;

    public EvalComment create(Long evaluationId, Long restaurantId, Long userId, EvalCommentRequest req) {
        EvalComment evalComment = EvalComment.create(userId, restaurantId, evaluationId, req);
        return evalCommentRepository.save(evalComment);
    }

    public void delete(Long evalCommentId, Long restaurantId, Long userId) {
        EvalComment evalComment = evalCommentRepository.findByIdAndRestaurantId(evalCommentId, restaurantId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND));
        evalComment.softDelete(userId);
        evalCommUserReactionRepository.deleteAllByEvalCommentId(evalCommentId);
        evalCommentRepository.save(evalComment);
    }
}
