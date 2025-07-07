package com.kustaurant.kustaurant.evaluation.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentReactionResponse;
import com.kustaurant.kustaurant.evaluation.comment.domain.EvalComment;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommUserReactionEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.jpa.EvalCommUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.comment.service.port.EvalCommentRepository;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvalCommUserReactionService {
    private final EvalCommUserReactionRepository evalCommentLikeRepository;
    private final EvalCommentRepository evalCommentRepository;

    @Transactional
    public EvalCommentReactionResponse toggleReaction(Long userId, Long commentId, ReactionType target) {

        EvalComment evalComment = evalCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        Optional<EvalCommUserReactionEntity> evalCommentLike = evalCommentLikeRepository.findByUserIdAndEvalCommentId(userId, commentId);

        ReactionType resultReaction;

        if (evalCommentLike.isEmpty()) {
            // 좋아요 또는 싫어요를 처음 누르는 경우
            evalCommentLikeRepository.save(new EvalCommUserReactionEntity(userId, commentId, target));

            if(target==ReactionType.LIKE) evalComment.adjustLikeCount(+1);
            else evalComment.adjustDislikeCount(+1);

            resultReaction = target;
        } else{
            EvalCommUserReactionEntity row = evalCommentLike.get();

            if (row.getReaction() == target) {
                // 같은 버튼 다시 누름 -> 취소
                evalCommentLikeRepository.delete(row);

                if(target==ReactionType.LIKE) evalComment.adjustLikeCount(-1);
                else evalComment.adjustDislikeCount(-1);

                resultReaction = null;
            } else {
                // 반대 버튼 -> 전환
                row.setReaction(target);

                if(target==ReactionType.LIKE){
                    evalComment.adjustLikeCount(+1);
                    evalComment.adjustDislikeCount(-1);
                } else {
                    evalComment.adjustLikeCount(-1);
                    evalComment.adjustDislikeCount(+1);
                }

                resultReaction = target;
            }
        }

        return new EvalCommentReactionResponse(
                evalComment.getId(),
                resultReaction,
                evalComment.getLikeCount(),
                evalComment.getDislikeCount()
        );
    }

}
