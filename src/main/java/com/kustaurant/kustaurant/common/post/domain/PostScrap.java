package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostScrap {
    private Integer scrapId;
    private Integer userId;
    private Integer postId;
    private final LocalDateTime createdAt;

    public static PostScrap from(PostScrapEntity entity) {
        return new PostScrap(
                entity.getScrapId(),
                entity.getUser().getUserId(),
                entity.getPost().getPostId(),
                entity.getCreatedAt()
        );
    }

    private PostScrap(Integer userId, Integer postId, LocalDateTime createdAt) {
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    public static PostScrap create(Integer userId, Integer postId) {
        return new PostScrap(userId, postId, LocalDateTime.now());
    }

}
