package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;

import java.time.LocalDateTime;

public record MyPostCommentResponse(
        Integer postId,
        String postCategory,
        String postTitle,
        String postcommentBody,
        Integer commentlikeCount,
        String timeAgo
) {
    
    /**
     * PostCommentDTOProjection을 MyPostCommentResponse로 변환
     */
    public static MyPostCommentResponse from(PostCommentDTOProjection projection) {
        return new MyPostCommentResponse(
                projection.postId(),
                projection.postCategory(),
                projection.postTitle(),
                extractShortCommentBody(projection.commentBody()),
                projection.getNetLikes(),
                calculateTimeAgo(projection.updatedAt() != null ? projection.updatedAt() : projection.createdAt())
        );
    }
    
    /**
     * 댓글 본문을 20자로 제한하여 요약
     */
    private static String extractShortCommentBody(String commentBody) {
        if (commentBody == null || commentBody.isEmpty()) {
            return "";
        }
        
        if (commentBody.length() > 20) {
            return commentBody.substring(0, 20) + "...";
        }
        
        return commentBody;
    }
    
    /**
     * 시간 경과 계산 (한국어)
     */
    private static String calculateTimeAgo(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long diffInMinutes = java.time.Duration.between(dateTime, now).toMinutes();

        if (diffInMinutes < 1) {
            return "방금 전";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + "분 전";
        } else if (diffInMinutes < 1440) { // 24시간
            return (diffInMinutes / 60) + "시간 전";
        } else {
            return (diffInMinutes / 1440) + "일 전";
        }
    }
}
