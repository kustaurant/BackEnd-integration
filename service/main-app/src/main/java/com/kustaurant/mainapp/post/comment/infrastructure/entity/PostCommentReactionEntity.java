package com.kustaurant.mainapp.post.comment.infrastructure.entity;

import com.kustaurant.mainapp.common.enums.ReactionType;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReaction;
import com.kustaurant.mainapp.post.comment.domain.PostCommentReactionId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name="post_comment_reaction")
public class PostCommentReactionEntity {
    @EmbeddedId
    private PostCommentReactionJpaId id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @CreationTimestamp
    @Column(name = "reacted_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime reactedAt;

    public static PostCommentReactionEntity from(PostCommentReaction domain) {
        return PostCommentReactionEntity.builder()
                .id(new PostCommentReactionJpaId(domain.getId().postCommentId(), domain.getId().userId()))
                .reaction(domain.getReaction())
                .reactedAt(LocalDateTime.now())
                .build();
    }

    public PostCommentReaction toModel() {
        return PostCommentReaction.builder()
                .id(new PostCommentReactionId(id.getPostCommentId(), id.getUserId()))
                .reaction(reaction)
                .build();
    }
}
