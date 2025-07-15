package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;

import java.time.LocalDateTime;

public record MyPostCommentResponse(
        Integer postId,
        String postCategory,
        String postTitle,
        String postcommentBody,
        Integer commentlikeCount,
        @JsonIgnore LocalDateTime createdAt
) {
    @JsonProperty
    public String timeAgo() {
        return TimeAgoUtil.toKor(createdAt);
    }

}
