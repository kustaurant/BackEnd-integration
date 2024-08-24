package com.kustaurant.restauranttier.tab4_community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeOrDislikeDTO {

    public LikeOrDislikeDTO(Integer likeCount, Integer dislikeCount, String status) {
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.status = status;
    }

    @Schema(description = "좋아요 수", example = "10")
    Integer likeCount;
    @Schema(description = "싫어요 수", example = "3")
    Integer dislikeCount;
    @Schema(description = "상태", example = "likeCreated")
    String status;
}
