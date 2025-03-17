
package com.kustaurant.kustaurant.common.user.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.*;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.post.infrastructure.Post;
import com.kustaurant.kustaurant.common.post.infrastructure.PostComment;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrap;
import com.kustaurant.kustaurant.global.webUser.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users_tbl")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(unique = true, nullable = false)
    private String providerId;

    @Column(unique = true)
    private String accessToken;
    @Column(unique = true)
    private String refreshToken;

    private String userPassword;
    @Column(unique = true)
    private String userEmail;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true, nullable = false)
    private String userNickname;
    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String loginApi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantComment> restaruantCommentList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Evaluation> evaluationList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Feedback> feedbackList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantFavorite> restaurantFavoriteList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentLike> restaurantCommentLikeList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentDislike> restaurantCommentDislikeList = new ArrayList<>();


    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Post> postList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostComment> postCommentList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostScrap> scrapList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentReport> restaurantCommentReportList = new ArrayList<>();

    @Builder
    public User(String providerId, String loginApi, String userPassword, String userEmail, String userPhoneNumber, String userNickname, UserRole userRole, String status, LocalDateTime createdAt) {
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.phoneNumber =userPhoneNumber;
        this.userNickname = userNickname;
        this.userRole = userRole;
        this.status = status;
        this.createdAt = createdAt;


    }



    public User updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }


    public String getRoleKey() {
        return this.userRole.getValue();
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "dislikeUserList")
    private List<Post> dislikePostList = new ArrayList<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "likeUserList")
    private List<Post> likePostList = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "dislikeUserList")
    private List<PostComment> dislikePostCommentList = new ArrayList<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "likeUserList")
    private List<PostComment> likePostCommentList = new ArrayList<>();

    public String getRankImg() {
        return RestaurantConstants.getIconImgUrl(this, "");
    }
}

