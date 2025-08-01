package com.kustaurant.kustaurant.user.mypage.infrastructure;

import com.kustaurant.kustaurant.user.mypage.domain.UserStats;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Table(name = "user_stats_tbl")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatsEntity {
    @Id
    @Column(name = "user_id")
    private Long id;

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

    private UserStatsEntity(Long userId) {
        this.id = userId;
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

}
