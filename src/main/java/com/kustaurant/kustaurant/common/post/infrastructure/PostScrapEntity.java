package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(name="post_scraps_tbl")
public class PostScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer scrapId;

    public PostScrapEntity(Integer scrapId, UserEntity UserEntity, PostEntity postEntity, LocalDateTime createdAt) {
        this.scrapId = scrapId;
        this.user = UserEntity;
        this.post = postEntity;
        this.createdAt = createdAt;

    }
    public PostScrapEntity(){

    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity user;

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity post;

    LocalDateTime createdAt;

    public PostScrap toDomain() {
        return new PostScrap(
                scrapId,
                user.getUserId(),
                post.getPostId(),
                createdAt
        );
    }
    public static PostScrapEntity from(PostScrap postScrap, UserEntity user, PostEntity post) {
        PostScrapEntity entity = new PostScrapEntity();
        entity.setUser(user);
        entity.setPost(post);
        entity.setCreatedAt(postScrap.getCreatedAt());
        return entity;
    }


}
