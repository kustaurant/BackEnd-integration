package com.kustaurant.mainapp.post.post.infrastructure.entity;

import com.kustaurant.mainapp.post.post.domain.PostReactionId;
import com.kustaurant.mainapp.post.post.domain.PostScrap;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name="post_scrap")
public class PostScrapEntity {
    @EmbeddedId
    private PostReactionJpaId id;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public PostScrap toModel() {
        return new PostScrap(new PostReactionId(id.getPostId(), id.getUserId()));
    }

    public static PostScrapEntity from(PostScrap domain) {
        return PostScrapEntity.builder()
                .id(new PostReactionJpaId(domain.getId().postId(), domain.getId().userId()))
                .build();
    }
}
