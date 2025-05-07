package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDislike {

    private Integer id;
    private final User user;
    private final Post post;
    private final LocalDateTime createdAt;

    public PostDislike(User user, Post post, LocalDateTime createdAt) {
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }
}
