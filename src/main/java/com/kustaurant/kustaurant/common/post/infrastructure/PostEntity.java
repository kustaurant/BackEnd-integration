package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
    private String status;
    private String postCategory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer postVisitCount = 0;
    private Integer likeCount = 0;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "postEntity")
    private List<PostComment> postCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "postEntity")
    private List<PostScrap> postScrapList = new ArrayList<>();

    @OneToMany(mappedBy = "postEntity")
    private List<PostPhoto> postPhotoList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "post_dislikes_tbl",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> dislikeUserList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "post_likes_tbl",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> likeUserList = new ArrayList<>();

    public PostEntity(String postTitle, String postBody, String postCategory, String status, LocalDateTime createdAt, User user) {
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postCategory = postCategory;
        this.status = status;
        this.createdAt = createdAt;
        this.user = user;
    }

    public PostEntity() {

    }

    public Post toDomain() {
        return new Post(
                postId, postTitle, postBody, postCategory, status,
                createdAt, updatedAt, postVisitCount, likeCount,
                user.getUserId()
        );
    }

    public static PostEntity fromDomain(Post post, User user) {
        return new PostEntity(
                post.getPostTitle(),
                post.getPostBody(),
                post.getPostCategory(),
                post.getStatus(),
                post.getCreatedAt(),
                user
        );
    }
}
