package com.kustaurant.mainapp.post.community.infrastructure.projection;

import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;

import java.time.LocalDateTime;

public record PostListProjection(
        Long postId,
        PostCategory category,
        String title,
        String body,
        Long writerId,
        String writerNickName,
        long writerEvalCount,
        String photoUrl,
        LocalDateTime createdAt,
        long totalLikes,
        long commentCount
) {
}
