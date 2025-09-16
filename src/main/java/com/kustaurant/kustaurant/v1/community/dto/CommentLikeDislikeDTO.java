package com.kustaurant.kustaurant.v1.community.dto;

import lombok.Data;

@Data
public class CommentLikeDislikeDTO {
    private int likeCount;
    private int dislikeCount;
    private int commentLikeStatus;

    public CommentLikeDislikeDTO(int totalLikeCount, int totalDislikeCount, int commentLikeStatus) {
        this.likeCount = totalLikeCount;
        this.dislikeCount = totalDislikeCount;
        this.commentLikeStatus = commentLikeStatus;
    }

    public static CommentLikeDislikeDTO toCommentLikeDislikeDTO(PostComment postComment, int commentLikeStatus) {
        return new CommentLikeDislikeDTO(postComment.getLikeUserList().size(), postComment.getDislikeUserList().size(), commentLikeStatus);
    }
}
