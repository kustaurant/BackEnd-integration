package com.kustaurant.kustaurant.user.mypage.controller.response;

public record MyPostCommentResponse(
        Integer postId,
        String postCategory,
        String postTitle,
        String postcommentBody,
        Integer commentlikeCount,
        String timeAgo
) {}
