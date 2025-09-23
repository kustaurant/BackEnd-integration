package com.kustaurant.mainapp.evaluation.evaluation.service;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.evaluation.evaluation.controller.response.EvalReactionResponse;
import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.EvaluationReactionEntity;
import com.kustaurant.mainapp.evaluation.evaluation.infrastructure.jpa.EvalUserReactionRepository;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.EvaluationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public EvalReactionResponse setEvaluationReaction(Long userId, Long evaluationId, ReactionType cmd) {
        Evaluation evaluation = evaluationQueryRepository.findActiveById(evaluationId);
        Optional<EvaluationReactionEntity> existingOpt = evaluationLikeRepository.findByEvaluationIdAndUserId(evaluationId, userId);
        var existing = existingOpt.orElse(null);

        if (cmd == null) {
            // 해제
            if (existing != null) {
                if (existing.getReaction() == ReactionType.LIKE) {
                    evaluation.adjustLikeCount(-1);
                } else {
                    evaluation.adjustDislikeCount(-1);
                }
                evaluationLikeRepository.delete(existing);
            }
        } else if (existing == null) {
            // 신규 설정
            try {
                evaluationLikeRepository.save(new EvaluationReactionEntity(userId, evaluationId, cmd));
                if (cmd == ReactionType.LIKE) evaluation.adjustLikeCount(+1);
                else evaluation.adjustDislikeCount(+1);
            } catch (DataIntegrityViolationException e) {
            }
        } else if (existing.getReaction() != cmd) {
            // 전환 (DISLIKE -> LIKE) 또는 (LIKE -> DISLIKE)
            if (cmd == ReactionType.LIKE) {
                evaluation.adjustLikeCount(+1);
                evaluation.adjustDislikeCount(-1);
            } else {
                evaluation.adjustLikeCount(-1);
                evaluation.adjustDislikeCount(+1);
            }
            existing.updateReaction(cmd);
        }

        // 좋아요, 싫어요 업데이트
        evaluationCommandRepository.react(evaluation);

        return new EvalReactionResponse(
                evaluation.getId(),
                cmd,
                evaluation.getLikeCount(),
                evaluation.getDislikeCount()
        );
    }

}
