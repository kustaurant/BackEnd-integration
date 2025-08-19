package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="post_scraps")
public class PostScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer scrapId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name="post_id", nullable = false)
    private Integer postId;

    private LocalDateTime createdAt;

    @Builder
    private PostScrapEntity (
            Integer scrapId,
            Long userId,
            Integer postId,
            LocalDateTime createdAt
    ) {
        this.scrapId = scrapId;
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }


    public PostScrap toModel() {
        return new PostScrap(
                scrapId,
                userId,
                postId,
                createdAt
        );
    }
    public static PostScrapEntity from(PostScrap postScrap) {
        return PostScrapEntity.builder()
                .scrapId(postScrap.getScrapId())
                .userId(postScrap.getUserId())
                .postId(postScrap.getPostId())
                .createdAt(postScrap.getCreatedAt())
                .build();
    }
}
