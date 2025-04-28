package com.kustaurant.kustaurant.common.post.domain;

import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostScrap {
    private final Integer scrapId;
    private final Integer userId;
    private final Post post;
    private final LocalDateTime createdAt;

    public static PostScrap from(PostScrapEntity entity) {
        return new PostScrap(
                entity.getScrapId(),
                entity.getUser().getUserId(),
                entity.getPost().toDomain(),
                entity.getCreatedAt()
        );
    }

}
