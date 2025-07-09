package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvalUserReactionEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvalUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvalUserReactionService {
    private final EvaluationRepository evaluationRepository;
    private final EvalUserReactionRepository evaluationLikeRepository;

    // 평가 좋아요/싫어요 토글
    @Transactional
    public EvalReactionResponse toggleReaction(Long userId, Long evaluationId, ReactionType reaction) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(()-> new DataNotFoundException(ErrorCode.EVALUATION_NOT_FOUND));
        Optional<EvalUserReactionEntity> evaluationLike = evaluationLikeRepository.findByEvaluationIdAndUserId(userId, evaluationId);

        ReactionType resultReaction;

        if (evaluationLike.isEmpty()) {
            evaluationLikeRepository.save(new EvalUserReactionEntity(userId, evaluationId, reaction));
            if(reaction.isLike()) evaluation.adjustLikeCount(+1);
            else evaluation.adjustDislikeCount(+1);

            resultReaction = reaction;
        } else {
            EvalUserReactionEntity row = evaluationLike.get();

            if(row.getReaction().equals(reaction)) {
                evaluationLikeRepository.delete(row);
                if(reaction==ReactionType.LIKE) evaluation.adjustLikeCount(-1);
                else evaluation.adjustDislikeCount(-1);

                resultReaction = null;
            } else {
                // 반대 버튼 -> 전환
                row.setReaction(reaction);

                if(reaction==ReactionType.LIKE){
                    evaluation.adjustLikeCount(+1);
                    evaluation.adjustDislikeCount(-1);
                } else {
                    evaluation.adjustLikeCount(-1);
                    evaluation.adjustDislikeCount(+1);
                }

                resultReaction = reaction;
            }
        }

        return new EvalReactionResponse(
                evaluation.getId(),
                resultReaction,
                evaluation.getLikeCount(),
                evaluation.getDislikeCount()
        );

    }

}
