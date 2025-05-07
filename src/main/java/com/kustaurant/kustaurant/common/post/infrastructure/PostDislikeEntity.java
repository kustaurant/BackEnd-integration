package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostDislike;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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

    public PostDislikeEntity(UserEntity user, PostEntity post, LocalDateTime createdAt) {
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }

    public PostDislikeEntity() {

    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostDislike toDomain() {
        return new PostDislike(
                this.user.toModel(),
                this.post.toDomain(),
                this.createdAt
        );
    }

    public static PostDislikeEntity from(PostDislike postDislike) {
        UserEntity userEntity = UserEntity.from(postDislike.getUser());
        return new PostDislikeEntity(
                userEntity,
                PostEntity.from(postDislike.getPost(), userEntity),
                postDislike.getCreatedAt()
        );
    }

    public static PostDislikeEntity from(PostDislike postDislike, UserEntity user, PostEntity post) {
        PostDislikeEntity postDislikeEntity = new PostDislikeEntity();
        postDislikeEntity.setUser(user);
        postDislikeEntity.setPost(post);
        postDislikeEntity.setCreatedAt(postDislike.getCreatedAt());
        return postDislikeEntity;
    }

}
