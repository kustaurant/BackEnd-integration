package com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.projection;

import com.kustaurant.kustaurant.common.enums.ReactionType;

import java.time.LocalDateTime;

public record CommentProjection(
        Long commentId,
        String writerNickname,
        Integer writerEvalCnt,
        LocalDateTime createdAt,
        String commentBody,
        ReactionType myReaction,
        Long likeCnt,
        Long dislikeCnt,
        Boolean isMine
) {

}
