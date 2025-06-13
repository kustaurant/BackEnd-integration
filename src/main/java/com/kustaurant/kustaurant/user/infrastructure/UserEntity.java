
package com.kustaurant.kustaurant.user.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentDislikeEntity;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.infrastructure.*;
import com.kustaurant.kustaurant.post.infrastructure.PostDislikeEntity;
import com.kustaurant.kustaurant.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.post.infrastructure.PostLikeEntity;
import com.kustaurant.kustaurant.post.infrastructure.PostScrapEntity;
import com.kustaurant.kustaurant.restaurant.infrastructure.entity.RestaurantFavoriteEntity;
import com.kustaurant.kustaurant.restaurant.application.constants.RestaurantConstants;
import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users_tbl")
@NoArgsConstructor
@Slf4j
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    @Column(unique = true, nullable = false)
    private String providerId;

    @Column(name = "email", unique = true)
    private String email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "phone_number", unique = true))
    private PhoneNumber phoneNumber;
    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", unique = true, nullable = false))
    private Nickname nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String loginApi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantComment> restaruantCommentList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<EvaluationEntity> evaluationList = new ArrayList<>();

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
    private List<PostCommentEntity> postCommentList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PostScrapEntity> scrapList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<RestaurantCommentReport> restaurantCommentReportList = new ArrayList<>();

    @Builder
    public UserEntity(
            String providerId,
            String loginApi,
            String email,
            PhoneNumber phoneNumber,
            Nickname userNickname,
            UserRole role,
            UserStatus status,
            LocalDateTime createdAt
    ) {
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.email = email;
        this.phoneNumber =phoneNumber;
        this.nickname = userNickname;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
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
        return UserEntity.builder()
                .providerId(user.getProviderId())
                .loginApi(user.getLoginApi())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public User toModel(){
        return User.builder()
                .id(userId)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .providerId(providerId)
                .loginApi(loginApi)
                .status(status)
                .createdAt(createdAt)
                // TODO: rankImg를 evaluactionCount에 맞게 설정해줘야함, 임시로 level1icon.png 사용
                .rankImg("https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level1icon.png")
                .build();
    }

}