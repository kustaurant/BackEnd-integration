package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name="post_reaction")
public class PostReactionEntity {
    @EmbeddedId
    private PostReactionJpaId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @CreationTimestamp
    @Column(name = "reacted_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime reactedAt;

    public static PostReactionEntity from(PostReaction domain) {
        return PostReactionEntity.builder()
                .id(new PostReactionJpaId(domain.getId().postId(), domain.getId().userId()))
                .reaction(domain.getReaction())
                .build();
    }

    public PostReaction toModel() {
        return PostReaction.builder()
                .id(new PostReactionId(id.getPostId(), id.getUserId()))
                .reaction(reaction)
                .build();
    }

}
