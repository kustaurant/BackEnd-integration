package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;

public record MyPostsResponse (
        Integer postId,
        String postCategory,
        String postTitle,
        String postImgUrl,
        String fullBody,
        Integer likeCount,
        Integer commentCount,
        @JsonIgnore LocalDateTime createdAt
){
    private static final int PREVIEW_LENGTH = 20;

    @JsonProperty("timeAgo")
    public String timeAgo() {
        return TimeAgoUtil.toKor(createdAt);
    }

    @JsonProperty("body")
    public String postBodyPreview() {
        if (fullBody == null) return "";
        return fullBody.length() <= PREVIEW_LENGTH
                ? fullBody
                : fullBody.substring(0, PREVIEW_LENGTH) + "…";
    }
}
