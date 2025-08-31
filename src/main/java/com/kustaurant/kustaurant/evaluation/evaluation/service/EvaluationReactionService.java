package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.entity.EvaluationReactionEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.jpa.EvalUserReactionRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EvaluationReactionService {

    private final EvaluationQueryRepository evaluationQueryRepository;
    private final EvaluationCommandRepository evaluationCommandRepository;
    private final EvalUserReactionRepository evaluationLikeRepository;

    // 평가 좋아요/싫어요 토글
    @Transactional
    public EvalReactionResponse toggleReaction(Long userId, Long evaluationId, ReactionType reaction) {
        Evaluation evaluation = evaluationQueryRepository.findActiveById(evaluationId);
        Optional<EvaluationReactionEntity> evaluationLike = evaluationLikeRepository.findByEvaluationIdAndUserId(evaluationId, userId);

        ReactionType resultReaction;

        if (evaluationLike.isEmpty()) {
            evaluationLikeRepository.save(new EvaluationReactionEntity(userId, evaluationId, reaction));
            if(reaction.isLike()) evaluation.adjustLikeCount(+1);
            else evaluation.adjustDislikeCount(+1);

            resultReaction = reaction;
        } else {
            EvaluationReactionEntity row = evaluationLike.get();

            if (row.getReaction() == reaction) {
                evaluationLikeRepository.delete(row);
                if (reaction==ReactionType.LIKE)
                    evaluation.adjustLikeCount(-1);
                else
                    evaluation.adjustDislikeCount(-1);

                resultReaction = null;
            } else {
                // 반대 버튼 -> 전환
                row.updateReaction(reaction);

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

        // 좋아요, 싫어요 업데이트
        evaluationCommandRepository.react(evaluation);

        return new EvalReactionResponse(
                evaluation.getId(),
                resultReaction,
                evaluation.getLikeCount(),
                evaluation.getDislikeCount()
        );

    }

}
