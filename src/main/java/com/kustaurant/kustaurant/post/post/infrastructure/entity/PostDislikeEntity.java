package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.post.domain.PostDislike;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_dislikes_tbl_new")
public class PostDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postDislikesId;

    @Column(name = "user_id" , nullable = false)
    Long userId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostDislikeEntity() {
    }
    public PostDislikeEntity(Long userId, PostEntity post, LocalDateTime createdAt) {
        this.userId = userId;
        this.post = post;
        this.createdAt = createdAt;
    }

    public PostDislike toDomain() {
        return new PostDislike(
                this.userId,
                this.post.getPostId(),
                this.createdAt
        );
    }

    public static PostDislikeEntity from(PostDislike postDislike) {
        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(postDislike.getPostId());

        return new PostDislikeEntity(
                postDislike.getUserId(),
                postEntity,
                postDislike.getCreatedAt()
        );
    }

    public static PostDislikeEntity from(PostDislike postDislike, Long userId, PostEntity post) {
        PostDislikeEntity postDislikeEntity = new PostDislikeEntity();
        postDislikeEntity.setUserId(userId);
        postDislikeEntity.setPost(post);
        postDislikeEntity.setCreatedAt(postDislike.getCreatedAt());
        return postDislikeEntity;
    }

}
