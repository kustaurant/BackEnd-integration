package com.kustaurant.kustaurant.post.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeOrDislikeDTO {

    public LikeOrDislikeDTO(Integer likeCount, Integer status) {
        this.likeCount = likeCount;
        this.status = status;
    }

    @Schema(description = "좋아요 수", example = "10")
    Integer likeCount;

    @Schema(description = "상태", example = "1")
    Integer status;
}
