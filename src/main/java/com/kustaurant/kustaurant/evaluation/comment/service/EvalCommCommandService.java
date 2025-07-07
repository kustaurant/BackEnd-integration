package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.evaluation.comment.controller.request.EvalCommentRequest;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.comment.service.port.EvalCommentRepository;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EvalCommCommandService {
    EvalCommentRepository evalCommentRepository;
    EvalCommUserReactionRepository evalCommUserReactionRepository;

    public EvalComment create(Long evalCommentId, Integer restaurantId,  Long userId, EvalCommentRequest req) {
        return EvalComment.create(userId, restaurantId, evalCommentId, req);
//        EvalComment evalComment = EvalComment.create(userId, restaurantId, evalCommentId, req);
//        return evalCommentRepository.save(evalComment);
    }

    public void delete(Long evalCommentId, Integer restaurantId, Long userId) {
        EvalComment evalComment = evalCommentRepository.findByIdAndRestaurantId(evalCommentId, restaurantId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        evalComment.softDelete(userId);
        evalCommUserReactionRepository.deleteAllByEvalCommentId(evalCommentId);
//        evalCommentRepository.save(evalComment);
    }
}
