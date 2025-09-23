package com.kustaurant.mainapp.post.comment.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.common.util.TimeAgoResolver;
import com.kustaurant.mainapp.post.comment.domain.PostComment;
import com.kustaurant.mainapp.common.dto.UserSummary;
import com.kustaurant.mainapp.user.user.domain.User;
import lombok.Builder;


import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostCommentResponse(
        Long commentId,
        Long parentCommentId,
        String body,
        String status,
        long likeCount,
        long dislikeCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String timeAgo,
        ReactionType reactionType,
        boolean isCommentMine,
        int replyCount,
        List<PostCommentResponse> repliesList,
        @JsonUnwrapped(prefix = "writer") UserSummary user
) {
    public static PostCommentResponse from(PostComment comment, User user) {
        return PostCommentResponse.builder()
                .commentId(comment.getId())
                .body(comment.getBody())
                .status(comment.getStatus().name())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .user(UserSummary.from(user))
                .likeCount(0)
                .dislikeCount(0)
                .timeAgo(TimeAgoResolver.toKor(comment.getCreatedAt()))
                .reactionType(null)
                .isCommentMine(true)
                .replyCount(0)
                .repliesList(null)
                .user(null)
                .build();
    }
}
