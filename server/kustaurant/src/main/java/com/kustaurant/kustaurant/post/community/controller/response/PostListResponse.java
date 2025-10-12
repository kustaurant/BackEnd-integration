package com.kustaurant.kustaurant.post.community.controller.response;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.common.util.TimeAgoResolver;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostListProjection;
import com.kustaurant.kustaurant.common.util.UserIconResolver;
import org.jsoup.Jsoup;

public record PostListResponse(
        Long postId,
        PostCategory category,
        String title,
        String body,
        @JsonUnwrapped(prefix = "writer") UserSummary writer,
        String photoUrl,
        String timeAgo,
        long totalLikes,
        long commentCount
) {
    public static PostListResponse from(PostListProjection p) {
        String excerpt = summarize(p.body(), 30);
        String userIconUrl = UserIconResolver.resolve(p.writerEvalCount());
        String timeAgo = TimeAgoResolver.toKor(p.createdAt());
        UserSummary writer = new UserSummary(p.writerId(), p.writerNickName(), p.writerEvalCount(),userIconUrl);

        return new PostListResponse(
                p.postId(),
                p.category(),
                p.title(),
                excerpt,
                writer,
                p.photoUrl(),
                timeAgo,
                p.totalLikes(),
                p.commentCount()
        );
    }

    private static String summarize(String htmlOrText, int maxLen) {
        if (htmlOrText == null || htmlOrText.isEmpty()) return "";
        // HTML 제거
        String text = Jsoup.parse(htmlOrText).text();
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen) + "…";
    }
}
