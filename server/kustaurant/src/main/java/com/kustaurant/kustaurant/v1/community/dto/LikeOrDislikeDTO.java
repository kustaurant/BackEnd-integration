package com.kustaurant.kustaurant.v1.community.dto;

import lombok.Data;

@Data
public class LikeOrDislikeDTO {

    public LikeOrDislikeDTO(Integer likeCount, Integer status) {
        this.likeCount = likeCount;
        this.status = status;
    }

    Integer likeCount;
    Integer status;
}
