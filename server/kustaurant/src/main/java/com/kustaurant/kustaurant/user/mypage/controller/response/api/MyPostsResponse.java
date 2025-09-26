package com.kustaurant.kustaurant.user.mypage.controller.response.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kustaurant.kustaurant.common.util.TimeAgoResolver;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;

public record MyPostsResponse (
        Long postId,
        PostCategory postCategory,
        String postTitle,
        String postImgUrl,
        String fullBody,
        Long likeCount,
        Long commentCount,
        @JsonIgnore LocalDateTime createdAt
){
    private static final int PREVIEW_LENGTH = 30;

    @JsonProperty("timeAgo")
    public String timeAgo() {
        return TimeAgoResolver.toKor(createdAt);
    }

    @JsonProperty("body")
    public String postBodyPreview() {
        if (fullBody == null) return "";
        String plain = Jsoup.parse(fullBody).text();
        plain = plain.replaceAll("\\s+", " ").trim();
        return plain.length() <= PREVIEW_LENGTH ? plain : plain.substring(0, PREVIEW_LENGTH) + "…";
    }
}
