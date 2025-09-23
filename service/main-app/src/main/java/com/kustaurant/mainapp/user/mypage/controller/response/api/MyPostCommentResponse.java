package com.kustaurant.mainapp.user.mypage.controller.response.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustaurant.mainapp.common.util.TimeAgoResolver;
import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;

import java.time.LocalDateTime;

public record MyPostCommentResponse(
        Long postId,
        PostCategory postCategory,
        String postTitle,
        String body,
        Long likeCount,
        @JsonIgnore LocalDateTime createdAt
) {
    @JsonProperty
    public String timeAgo() {
        return TimeAgoResolver.toKor(createdAt);
    }
}
