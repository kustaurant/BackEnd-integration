package com.kustaurant.kustaurant.post.infrastructure;

import com.kustaurant.kustaurant.post.domain.PostLike;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post_likes_tbl_new")
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
    @JoinColumn(name = "post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostLike toDomain() {
        return new PostLike(this.user.getId(), this.post.getPostId(), this.createdAt);
    }

    public static PostLikeEntity from(PostLike postLike) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(postLike.getUserId());

        PostEntity postEntity = new PostEntity();
        postEntity.setPostId(postLike.getPostId());
        return new PostLikeEntity(userEntity, postEntity, postLike.getCreatedAt());
    }

}
