package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name="post_dislikes_tbl_new")
public class PostDislikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postDislikesId;

    public PostDislikeEntity(User user, PostEntity postEntity, LocalDateTime createdAt) {
        this.user = user;
        this.postEntity = postEntity;
        this.createdAt = createdAt;
    }
    public PostDislikeEntity() {

    }
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity postEntity;

    LocalDateTime createdAt;
}
