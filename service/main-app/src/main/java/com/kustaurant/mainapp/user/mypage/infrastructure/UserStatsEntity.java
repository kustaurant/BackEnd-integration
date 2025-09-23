package com.kustaurant.mainapp.user.mypage.infrastructure;

import com.kustaurant.mainapp.user.mypage.domain.UserStats;
import com.kustaurant.mainapp.user.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_stats")
public class UserStatsEntity {
    @Id
    @Column(name = "user_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private int savedRestCnt;
    private int ratedRestCnt;
    private int commPostCnt;
    private int commCommentCnt;
    private int commSavedPostCnt;

    private UserStatsEntity(Long userId) {
        this.id = userId;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public static UserStatsEntity of(Long userId, @Nullable UserStats stats) {
        UserStatsEntity statsEntity = new UserStatsEntity(userId);
        if (stats != null) {
            statsEntity.savedRestCnt     = stats.getSavedRestCnt();
            statsEntity.ratedRestCnt     = stats.getRatedRestCnt();
            statsEntity.commPostCnt      = stats.getCommPostCnt();
            statsEntity.commCommentCnt   = stats.getCommCommentCnt();
            statsEntity.commSavedPostCnt = stats.getCommSavedPostCnt();
        }
        return statsEntity;
    }

    public UserStats toModel() {
        return UserStats.builder()
                .id(id)
                .savedRestCnt(savedRestCnt)
                .ratedRestCnt(ratedRestCnt)
                .commPostCnt(commPostCnt)
                .commCommentCnt(commCommentCnt)
                .commSavedPostCnt(commSavedPostCnt)
                .build();
    }


}
