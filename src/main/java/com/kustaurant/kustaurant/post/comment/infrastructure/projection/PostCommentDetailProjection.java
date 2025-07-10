package com.kustaurant.kustaurant.post.comment.infrastructure.projection;

import java.time.LocalDateTime;

/**
 * Projection for post comment detail view with user interaction states
 * Used in post detail page to display comments with like/dislike states
 */
public record PostCommentDetailProjection(
        Integer commentId,
        String commentBody,
        String status,
        Integer parentCommentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer postId,
        Long userId,
        String userNickname,
        Integer userEvaluationCount,
        Long likeCount,
        Long dislikeCount,
        Boolean isLiked,
        Boolean isDisliked,
        Boolean isMine
) {
    
    public Integer getNetLikes() {
        return Math.toIntExact((likeCount != null ? likeCount : 0L) - (dislikeCount != null ? dislikeCount : 0L));
    }
    
    public Integer getLikeOnlyCount() {
        return Math.toIntExact(likeCount != null ? likeCount : 0L);
    }
    
    public Integer getDislikeOnlyCount() {
        return Math.toIntExact(dislikeCount != null ? dislikeCount : 0L);
    }
}