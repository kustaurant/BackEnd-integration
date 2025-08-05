package com.kustaurant.kustaurant.post.comment.controller.response;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReactionResponse {
    @Schema(description = "댓글 좋아요 수", example = "10")
    private int likeCount;
    @Schema(description = "댓글 싫어요 수", example = "3")
    private int dislikeCount;
    private int commentLikeStatus;

    public CommentReactionResponse(int totalLikeCount, int totalDislikeCount, int commentLikeStatus) {
        this.likeCount = totalLikeCount;
        this.dislikeCount = totalDislikeCount;
        this.commentLikeStatus = commentLikeStatus;
    }

    public static CommentReactionResponse toCommentLikeDislikeDTO(PostComment postComment, int commentLikeStatus) {
        return new CommentReactionResponse(0, 0, commentLikeStatus); // 리액션 지표는 별도 서비스에서 계산
    }

}
