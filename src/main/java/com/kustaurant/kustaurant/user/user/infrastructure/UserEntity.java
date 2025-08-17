
package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.common.infrastructure.BaseTimeEntity;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.mypage.infrastructure.UserStatsEntity;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@SQLDelete(sql = "update users_tbl set status = 'DELETED' where user_id = ?")
@SQLRestriction("status = 'ACTIVE'")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users_tbl")
public class UserEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "login_api", length = 10, nullable = false)
    private LoginApi loginApi;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @OneToOne(mappedBy = "user", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "user_id")
    private UserStatsEntity stats;

    @Builder
    public UserEntity(
            String providerId,
            LoginApi loginApi,
            String email,
            PhoneNumber phoneNumber,
            Nickname userNickname,
            UserRole role,
            UserStatus status,
            UserStatsEntity stats
    ) {
        this.providerId = providerId;
        this.loginApi = loginApi;
        this.email = email;
        this.phoneNumber =phoneNumber;
        this.nickname = userNickname;
        this.role = role;
        this.status = status;
        this.stats = stats;
    }

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .loginApi(user.getLoginApi())
                .providerId(user.getProviderId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userNickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .stats(UserStatsEntity.of(user.getId(), user.getStats()))
                .build();
    }

    public User toModel(){
        return User.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .providerId(providerId)
                .loginApi(loginApi)
                .createdAt(getCreatedAt())
                .status(status)
                .stats(stats.toModel())
                .build();
    }
}