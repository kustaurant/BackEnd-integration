package com.kustaurant.restauranttier.tab4_community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentLikeDislikeDTO {
    private int totalLikeCount;
    private int totalDislikeCount;
    private int commentLikeStatus;

    public CommentLikeDislikeDTO(int totalLikeCount, int totalDislikeCount, int commentLikeStatus) {
        this.totalLikeCount = totalLikeCount;
        this.totalDislikeCount = totalDislikeCount;
        this.commentLikeStatus = commentLikeStatus;
    }


}
