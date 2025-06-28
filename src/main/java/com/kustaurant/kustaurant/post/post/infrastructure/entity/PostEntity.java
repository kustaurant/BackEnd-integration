package com.kustaurant.kustaurant.post.post.infrastructure.entity;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.PostPhoto;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts_tbl")
@FilterDef(name = "activePostFilter", parameters = @ParamDef(name = "status", type = String.class))
@Filter(name = "activePostFilter", condition = "status = :status")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    private String postTitle;
    private String postBody;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(20)")
    private ContentStatus status;

    private String postCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer postVisitCount = 0;
    private Integer netLikes = 0;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "post")
    private List<PostCommentEntity> postCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostPhotoEntity> postPhotoEntityList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostLikeEntity> postLikesList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostDislikeEntity> postDislikesList = new ArrayList<>();

    public PostEntity(String postTitle, String postBody, String postCategory, ContentStatus status, LocalDateTime createdAt, Long userId) {
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postCategory = postCategory;
        this.status = status;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public PostEntity() {

    }

    public Post toModel() {
        return Post.builder()
                .id(postId)
                .title(postTitle)
                .body(postBody)
                .category(postCategory)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .visitCount(postVisitCount)
                .netLikes(netLikes)
                .likeCount(postLikesList.size())
                .dislikeCount(postDislikesList.size())
                .authorId(userId)
                .photos(postPhotoEntityList.stream()
                        .map(PostPhotoEntity::toDomain)
                        .toList())
                .comments(postCommentList.stream().map(PostCommentEntity::toDomain).toList())
                .build();
    }

    public Post toModel(
            boolean includeComments,
            boolean includePhotos,
            boolean includeScraps,
            boolean includeLikes,
            boolean includeDislikes
    ) {
        Post.PostBuilder postBuilder = Post.builder()
                .id(postId)
                .title(postTitle)
                .body(postBody)
                .category(postCategory)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .visitCount(postVisitCount)
                .netLikes(netLikes)
                .authorId(userId);

        if (includeComments) {
            List<PostComment> comments = postCommentList.stream()
                    .map(PostCommentEntity::toDomain)
                    .toList();
            postBuilder.comments(comments);
        }

        if (includePhotos) {
            List<PostPhoto> photos = postPhotoEntityList.stream()
                    .map(PostPhotoEntity::toDomain)
                    .toList();
            postBuilder.photos(photos);
        }


        return postBuilder.build();
    }


    public static PostEntity from(Post post) {
        return new PostEntity(
                post.getTitle(),
                post.getBody(),
                post.getCategory(),
                post.getStatus(),
                post.getCreatedAt(),
                post.getAuthorId()
        );
    }

}