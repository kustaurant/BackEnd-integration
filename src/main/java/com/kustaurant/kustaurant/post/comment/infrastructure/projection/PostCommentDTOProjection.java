package com.kustaurant.kustaurant.post.comment.infrastructure.projection;

import java.time.LocalDateTime;

/**
 * 댓글 관련 데이터를 효율적으로 조회하기 위한 프로젝션 클래스
 * 댓글 + 게시글 + 사용자 정보를 포함하여 N+1 문제를 해결
 */
public record PostCommentDTOProjection(
        Integer commentId,
        String commentBody,
        String status,
        Integer parentCommentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Integer postId,
        Long userId,
        String userNickname,
        String userRankImg,
        Integer userEvaluationCount,
        Long likeCount,
        Long dislikeCount,
        String postTitle,
        String postCategory
) {
    
    /**
     * 댓글의 순 좋아요 수 계산 (좋아요 - 싫어요)
     */
    public Integer getNetLikes() {
        if (likeCount == null || dislikeCount == null) {
            return 0;
        }
        return Math.toIntExact(likeCount - dislikeCount);
    }
    
    /**
     * 댓글의 좋아요 수만 반환
     */
    public Integer getLikeOnlyCount() {
        return likeCount != null ? Math.toIntExact(likeCount) : 0;
    }
    
    /**
     * 댓글의 싫어요 수만 반환
     */
    public Integer getDislikeOnlyCount() {
        return dislikeCount != null ? Math.toIntExact(dislikeCount) : 0;
    }
}