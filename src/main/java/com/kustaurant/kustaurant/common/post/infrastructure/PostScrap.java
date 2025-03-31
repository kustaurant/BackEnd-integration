package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name="post_scraps_tbl")
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer scrapId;

    public PostScrap(UserEntity UserEntity, PostEntity postEntity, LocalDateTime createdAt) {
        this.user = UserEntity;
        this.postEntity = postEntity;
        this.createdAt = createdAt;
    }
    public PostScrap(){

    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity postEntity;

    LocalDateTime createdAt;

}
