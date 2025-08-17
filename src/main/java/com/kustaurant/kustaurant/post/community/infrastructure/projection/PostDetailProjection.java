package com.kustaurant.kustaurant.post.community.infrastructure.projection;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;

import java.time.LocalDateTime;

public record PostDetailProjection(
        // 게시글
        Integer postId,
        PostCategory category,
        String title,
        String body,
        // 작성자
        Long writerId,
        String writerNickName,
        long writerEvalCount,
        // 시간
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // 집계 수치
        int visitCount,
        long likeOnlyCount,
        long dislikeOnlyCount,
        long commentCount,
        long scrapCount,
        // 상호작용 정보
        ReactionType myReaction,
        boolean isScrapped
) {
}
