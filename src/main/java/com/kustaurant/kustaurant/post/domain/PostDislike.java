package com.kustaurant.kustaurant.post.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDislike {

    private Integer id;
    private Integer userId;
    private Integer postId;
    private final LocalDateTime createdAt;

    public PostDislike(Integer userId, Integer postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }
}
