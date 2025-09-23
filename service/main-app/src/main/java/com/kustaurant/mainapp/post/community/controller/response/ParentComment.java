package com.kustaurant.mainapp.post.community.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kustaurant.mainapp.common.dto.UserSummary;
import com.kustaurant.mainapp.common.enums.ReactionType;

import java.util.List;

public record ParentComment(
        long commentId,
        String body,
        String status,
        long likeCount,
        long dislikeCount,
        String timeAgo,
        ReactionType reactionType,
        boolean isCommentMine,
        int replyCount,
        List<CommentReply> replies,
        @JsonUnwrapped(prefix = "writer") UserSummary user
) {
}
