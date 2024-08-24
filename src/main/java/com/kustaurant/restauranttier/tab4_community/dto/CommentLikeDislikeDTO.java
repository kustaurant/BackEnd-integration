package com.kustaurant.restauranttier.tab4_community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeDislikeDTO {
    @Schema(description = "댓글 좋아요 수", example = "10")
    private int likeCount;
    @Schema(description = "댓글 싫어요 수", example = "3")
    private int dislikeCount;
    private int commentLikeStatus;

    public CommentLikeDislikeDTO(int totalLikeCount, int totalDislikeCount, int commentLikeStatus) {
        this.likeCount = totalLikeCount;
        this.dislikeCount = totalDislikeCount;
        this.commentLikeStatus = commentLikeStatus;
    }


}
