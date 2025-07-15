
package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.user.mypage.infrastructure.UserStatsEntity;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@Table(name = "users_tbl")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'ACTIVE'")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false)
    private String loginApi;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(fetch = LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private UserStatsEntity stats;

    @Transient
    private String rankImg;

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
        // TODO: stats 엔티티가 정상적으로 매핑되면 하드코딩 제거
        int evalCnt = (stats != null) ? stats.getRatedRestCnt() : 0;

        return User.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .providerId(providerId)
                .loginApi(loginApi)
                .status(status)
                .createdAt(createdAt)
                .rankImg(UserIconResolver.resolve(evalCnt))
                .build();
    }

}