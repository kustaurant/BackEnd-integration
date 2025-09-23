package com.kustaurant.mainapp.post.community.infrastructure.projection;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.post.domain.enums.PostCategory;

import java.time.LocalDateTime;

public record PostDetailProjection(
        // 게시글
        Long postId,
        PostCategory category,
        String title,
        String body,
        // 작성자
        Long writerId,
        String writerNickName,
        Long writerEvalCount,
        // 시간
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // 집계 수치
        Integer visitCount,
        Long likeOnlyCount,
        Long dislikeOnlyCount,
        Long commentCount,
        Long scrapCount,
        // 상호작용 정보
        ReactionType myReaction,
        Boolean isScrapped
) {
}
