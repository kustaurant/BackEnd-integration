package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.user.mypage.controller.response.UserActivityStatsResponse;
import com.kustaurant.kustaurant.user.mypage.infrastructure.UserStatsEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MypageStatsQueryRepository extends Repository<UserStatsEntity, Long> {
    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.controller.response.UserActivityStatsResponse(
            s.savedRestCnt,
            s.ratedRestCnt,
            s.commPostCnt,
            s.commCommentCnt,
            s.commSavedPostCnt
        )
        from UserStatsEntity s
        where s.id = :userId
    """)
    UserActivityStatsResponse findStatsByUserId(@Param("userId") Long userId);
}
