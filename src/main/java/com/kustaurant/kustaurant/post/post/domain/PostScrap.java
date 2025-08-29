package com.kustaurant.kustaurant.post.post.domain;

import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostScrapEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostScrap {
    private Integer scrapId;
    private Long userId;
    private Long postId;
    private final LocalDateTime createdAt;

    public static PostScrap from(PostScrapEntity entity) {
        return PostScrap.builder()
                .scrapId(entity.getScrapId())
                .userId(entity.getUserId())
                .postId(entity.getPostId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
