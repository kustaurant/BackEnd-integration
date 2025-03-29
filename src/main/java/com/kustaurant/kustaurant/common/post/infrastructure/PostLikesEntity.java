package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="post_likes_tbl_new")
public class PostLikesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postLikesId;

    public PostLikesEntity(User user, PostEntity postEntity) {
        this.user = user;
        this.postEntity = postEntity;
        this.createdAt = LocalDateTime.now();
    }
    public PostLikesEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity postEntity;

    LocalDateTime createdAt;
}
