package com.kustaurant.kustaurant.user.mypage.domain;

import com.kustaurant.kustaurant.user.mypage.infrastructure.UserStatsEntity;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class UserStats {
    private Long id;
    private int savedRestCnt;
    private int ratedRestCnt;
    private int commPostCnt;
    private int commCommentCnt;
    private int commSavedPostCnt;

    public static UserStats createEmpty(Long id) {
        return new UserStats(id, 0, 0, 0, 0, 0);
    }

    public static UserStats from(UserStatsEntity e) {
        return new UserStats(
                e.getId(),
                e.getSavedRestCnt(),
                e.getRatedRestCnt(),
                e.getCommPostCnt(),
                e.getCommCommentCnt(),
                e.getCommSavedPostCnt()
        );
    }


}
