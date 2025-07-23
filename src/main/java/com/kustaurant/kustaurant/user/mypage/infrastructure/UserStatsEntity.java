package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "user_stats_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private UserStatsEntity(UserEntity user) {
        this.user = user;
    }
    public static UserStatsEntity of(UserEntity user, @Nullable UserStats stats) {
        UserStatsEntity statsEntity = new UserStatsEntity(user);
        if (stats != null) {
            statsEntity.savedRestCnt     = stats.getSavedRestCnt();
            statsEntity.ratedRestCnt     = stats.getRatedRestCnt();
            statsEntity.commPostCnt      = stats.getCommPostCnt();
            statsEntity.commCommentCnt   = stats.getCommCommentCnt();
            statsEntity.commSavedPostCnt = stats.getCommSavedPostCnt();
        }
        return statsEntity;
    }

}
