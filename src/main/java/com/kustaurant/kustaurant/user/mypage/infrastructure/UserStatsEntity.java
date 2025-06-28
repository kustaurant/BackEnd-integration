package com.kustaurant.kustaurant.user.mypage.infrastructure;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
