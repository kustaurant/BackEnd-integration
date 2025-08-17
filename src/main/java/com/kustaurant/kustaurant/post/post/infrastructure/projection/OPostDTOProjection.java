package com.kustaurant.kustaurant.post.post.infrastructure.projection;

import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;

import java.time.LocalDateTime;

public record OPostDTOProjection(
        Integer postId,
        String postTitle,
        String postBody,
        PostCategory postCategory,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer visitCount,
        Long authorId,
        String authorNickname,
        Integer authorEvaluationCount,
        Long likeCount,
        Long dislikeCount,
        Long commentCount,
        Long scrapCount,
        String firstPhotoUrl,
        Boolean isLiked,
        Boolean isScraped
) {
    public Integer getNetLikes() {
        return Math.toIntExact(likeCount - dislikeCount);
    }

}