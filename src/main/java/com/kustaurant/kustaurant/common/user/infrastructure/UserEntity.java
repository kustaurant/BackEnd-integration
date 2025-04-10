
package com.kustaurant.kustaurant.common.user.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.common.comment.PostComment;
import com.kustaurant.kustaurant.common.comment.PostCommentDislikeEntity;
import com.kustaurant.kustaurant.common.comment.PostCommentLikeEntity;
import com.kustaurant.kustaurant.common.evaluation.infrastructure.*;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.common.restaurant.application.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.common.user.domain.vo.PhoneNumber;
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
public class UserEntity {

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

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number", unique = true))
    private PhoneNumber phoneNumber;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "user_nickname", unique = true, nullable = false))
    private Nickname userNickname;

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
//    @JsonIgnore
//    @OneToMany(mappedBy = "user")
//    private List<FeedbackEntity> feedbackList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantFavoriteEntity> restaurantFavoriteList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentLike> restaurantCommentLikeList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentDislike> restaurantCommentDislikeList = new ArrayList<>();


    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostEntity> postList = new ArrayList<>();

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
    public UserEntity(String providerId, String loginApi, String userPassword, String userEmail, PhoneNumber userPhoneNumber, Nickname userNickname, UserRole userRole, String status, LocalDateTime createdAt) {
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


    public UserEntity updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }


    public String getRoleKey() {
        return this.userRole.getValue();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostLikeEntity> postLikesList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostDislikeEntity> postDislikesList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostCommentLikeEntity> postCommentLikesEntities = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostCommentDislikeEntity> postCommentDislikesEntities = new ArrayList<>();

    public String getRankImg() {
        return RestaurantConstants.getIconImgUrl(this, "");
    }

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.userId=user.getId();
        userEntity.userNickname=user.getNickname();
        userEntity.userEmail=user.getEmail();
        userEntity.phoneNumber=user.getPhoneNumber();
        userEntity.userRole=user.getRole();
        userEntity.providerId=user.getProviderId();
        userEntity.loginApi=user.getLoginApi();
        userEntity.accessToken=user.getAccessToken();
        userEntity.refreshToken=user.getRefreshToken();
        userEntity.status=user.getStatus();
        userEntity.createdAt=user.getCreatedAt();

        return userEntity;
    }

    public User toModel(){
        return User.builder()
                .id(userId)
                .nickname(userNickname)
                .email(userEmail)
                .phoneNumber(phoneNumber)
                .role(userRole)
                .providerId(providerId)
                .loginApi(loginApi)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .status(status)
                .createdAt(createdAt)
                .build();
    }

}