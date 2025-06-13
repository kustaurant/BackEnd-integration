package com.kustaurant.kustaurant.post.domain;

import com.kustaurant.kustaurant.post.infrastructure.PostScrapEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class PostScrap {
    private Integer scrapId;
    private Integer userId;
    private Integer postId;
    private final LocalDateTime createdAt;

    public static PostScrap from(PostScrapEntity entity) {
        return PostScrap.builder()
                .scrapId(entity.getScrapId())
                .userId(entity.getUser().getUserId())
                .postId(entity.getPost().getPostId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
