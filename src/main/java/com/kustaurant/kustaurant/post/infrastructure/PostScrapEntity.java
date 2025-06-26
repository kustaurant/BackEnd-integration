package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name="post_scraps_tbl")
public class PostScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer scrapId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity post;

    private LocalDateTime createdAt;

    @Builder
    private PostScrapEntity(
            Integer scrapId,
            Long userId,
            PostEntity post,
            LocalDateTime createdAt
    ) {
        this.scrapId = scrapId;
        this.userId    = userId;
        this.post      = post;
        this.createdAt = createdAt;
    }


    public PostScrap toDomain() {
        return new PostScrap(
                scrapId,
                userId,
                post.getPostId(),
                createdAt
        );
    }
    public static PostScrapEntity from(PostScrap postScrap, PostEntity post) {
        return PostScrapEntity.builder()
                .scrapId(postScrap.getScrapId())
                .userId(postScrap.getUserId())
                .post(post)
                .createdAt(postScrap.getCreatedAt())
                .build();
    }
}
