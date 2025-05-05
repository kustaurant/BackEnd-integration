package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostDislike {

    private final Integer id;
    private final User user;
    private final Post post;
    private final LocalDateTime createdAt;

    public PostDislike(Integer id, User user, Post post, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }
}
