package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
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
                TimeAgoUtil.toKor(projection.createdAt()));
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
}
