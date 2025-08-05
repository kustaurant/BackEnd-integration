package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.*;

@Entity
@Getter
@SQLRestriction("status = 'ACTIVE'")
@SQLDelete(sql = "update posts_tbl set status = 'DELETED' where post_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "posts_tbl")
public class PostEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    private String postTitle;
    private String postBody;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private PostStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "post_category", columnDefinition = "varchar(20)")
    private PostCategory postCategory;
    private Integer postVisitCount = 0;
    private Integer netLikes = 0;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder
    public PostEntity(
            String postTitle,
            String postBody,
            PostCategory postCategory,
            PostStatus status,
            Long userId
    ) {
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postCategory = postCategory;
        this.status = status;
        this.userId = userId;
    }

    public Post toModel() {
        return Post.builder()
                .id(postId)
                .title(postTitle)
                .body(postBody)
                .category(postCategory)
                .status(status)
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .visitCount(postVisitCount)
                .writerId(userId)
                .build();
    }

    public static PostEntity from(Post post) {
        return PostEntity.builder()
                .postTitle(post.getTitle())
                .postBody(post.getBody())
                .postCategory(post.getCategory())
                .status(post.getStatus())
                .userId(post.getWriterId())
                .build();
    }

}