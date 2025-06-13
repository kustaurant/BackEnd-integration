package com.kustaurant.kustaurant.post.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostLike {

    private Integer id;
    private Integer userId;
    private Integer postId;
    private final LocalDateTime createdAt;

    public PostLike(Integer userId, Integer postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }


}