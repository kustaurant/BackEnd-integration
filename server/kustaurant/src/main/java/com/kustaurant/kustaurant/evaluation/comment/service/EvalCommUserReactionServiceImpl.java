package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.port.EvalCommentReactionService;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvaluationCommentReactionEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.comment.service.port.EvalCommentRepository;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvalCommUserReactionServiceImpl implements EvalCommentReactionService {
    private final EvalCommUserReactionRepository userReactionRepo;
    private final EvalCommentRepository evalCommentRepo;

    @Transactional
    public EvalCommentReactionResponse setEvalCommentReaction(Long userId, Long evalCommentId, @Nullable ReactionType cmd) {

        EvalComment evalComment = evalCommentRepo.findById(evalCommentId).orElseThrow(() -> new DataNotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        Optional<EvaluationCommentReactionEntity> existingOpt = userReactionRepo.findByUserIdAndEvalCommentId(userId, evalCommentId);
        var existing = existingOpt.orElse(null);

        if (cmd == null) { // 해제
            if (existing != null) {
                if (existing.getReaction() == ReactionType.LIKE) evalComment.adjustLikeCount(-1);
                else evalComment.adjustDislikeCount(-1);
                userReactionRepo.delete(existing);
            }
        } else if (existing == null) { // 신규 설정
            try {
                userReactionRepo.save(new EvaluationCommentReactionEntity(evalCommentId, userId, cmd));
                if (cmd == ReactionType.LIKE) evalComment.adjustLikeCount(+1);
                else evalComment.adjustDislikeCount(+1);
            } catch (DataIntegrityViolationException e) {
            }
        } else if (existing.getReaction() != cmd) { // 전환
            if (cmd == ReactionType.LIKE) { // DISLIKE -> LIKE
                evalComment.adjustLikeCount(+1);
                evalComment.adjustDislikeCount(-1);
            } else { // LIKE -> DISLIKE
                evalComment.adjustLikeCount(-1);
                evalComment.adjustDislikeCount(+1);
            }
            existing.setReaction(cmd);
            userReactionRepo.save(existing);
        }

        evalCommentRepo.save(evalComment);

        return new EvalCommentReactionResponse(
                evalComment.getId(),
                cmd,
                evalComment.getLikeCount(),
                evalComment.getDislikeCount()
        );
    }

}
