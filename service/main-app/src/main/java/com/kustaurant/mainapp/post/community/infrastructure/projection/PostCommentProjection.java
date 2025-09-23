package com.kustaurant.mainapp.post.community.infrastructure.projection;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.comment.domain.PostCommentStatus;

import java.time.LocalDateTime;

public record PostCommentProjection(
        Long commentId,
        Long parentCommentId,
        String body,
        PostCommentStatus status,
        Long writerId,
        String writerNickname,
        Long  writerEvalCount,
        LocalDateTime createdAt,
        Long  likeCount,
        Long  dislikeCount,
        ReactionType myReaction
) {
}
