package com.kustaurant.kustaurant.post.community.controller.response;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostListProjection;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import org.jsoup.Jsoup;

public record PostListResponse(
        Integer postId,
        PostCategory category,
        String title,
        String body,
        Long writerId,
        String writerNickname,
        String writerIconUrl,
        String photoUrl,
        String timeAgo,
        long totalLikes,
        long commentCount
) {
    public static PostListResponse from(PostListProjection p) {
        String excerpt = summarize(p.body(), 30);
        String userIconUrl = UserIconResolver.resolve(p.writerEvalCount());
        String timeAgo = TimeAgoUtil.toKor(p.createdAt());

        return new PostListResponse(
                p.postId(),
                p.category(),
                p.title(),
                excerpt,
                p.writerId(),
                p.writerNickName(),
                userIconUrl,
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
