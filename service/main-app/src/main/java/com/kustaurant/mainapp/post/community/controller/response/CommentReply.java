package com.kustaurant.mainapp.post.community.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kustaurant.mainapp.common.dto.UserSummary;
import com.kustaurant.mainapp.common.enums.ReactionType;

public record CommentReply(
        long commentId,
        Long parentCommentId,
        String body,
        String status,
        long likeCount,
        long dislikeCount,
        String timeAgo,
        ReactionType reactionType,
        boolean isCommentMine,
        @JsonUnwrapped(prefix = "writer") UserSummary user
) {
}
