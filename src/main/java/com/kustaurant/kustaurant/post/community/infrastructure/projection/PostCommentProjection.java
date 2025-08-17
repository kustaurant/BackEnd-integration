package com.kustaurant.kustaurant.post.community.infrastructure.projection;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;

import java.time.LocalDateTime;

public record PostCommentProjection(
        Integer commentId,
        Integer parentCommentId,
        String body,
        PostCommentStatus status,
        Long writerId,
        String writerNickname,
        long writerEvalCount,
        LocalDateTime createdAt,
        long likeCount,
        long dislikeCount,
        ReactionType myReaction
) {
}
