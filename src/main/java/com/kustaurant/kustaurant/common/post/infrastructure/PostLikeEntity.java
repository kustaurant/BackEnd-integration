package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.post.domain.PostLike;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_likes_tbl_new")
public class PostLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postLikesId;

    public PostLikeEntity(UserEntity user, PostEntity post, LocalDateTime createdAt) {
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }
    public PostLikeEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostLike toDomain() {
        return new PostLike(
                this.postLikesId,
                this.user.toModel(),
                this.post.toDomain(),
                this.createdAt
        );
    }

    public static PostLikeEntity from(PostLike postLike) {
        UserEntity userEntity = UserEntity.from(postLike.getUser());
        return new PostLikeEntity(
                userEntity,
                PostEntity.from(postLike.getPost(), userEntity),
                postLike.getCreatedAt()
        );
    }

    public static PostLikeEntity from(PostLike postLike, UserEntity user, PostEntity post) {
        PostLikeEntity postLikeEntity = new PostLikeEntity();
        postLikeEntity.setUser(user);
        postLikeEntity.setPost(post);
        postLikeEntity.setCreatedAt(postLike.getCreatedAt());
        return postLikeEntity;
    }

}
