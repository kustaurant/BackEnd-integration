package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.user.mypage.controller.response.ProfileResponse;
import com.kustaurant.kustaurant.user.mypage.infrastructure.projection.MypageMainProjection;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface MypageQueryRepository extends Repository<UserEntity, Long> {

    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.infrastructure.projection.MypageMainProjection(
            u.nickname.value,
            s.ratedRestCnt,
            s.commPostCnt
        )
        from UserEntity u
        join UserStatsEntity s on s.id = u.id
        where u.id = :userId
    """)
    MypageMainProjection findMypageMainByUserId(@Param("userId") Long userId);

    @Query("""
        select new com.kustaurant.kustaurant.user.mypage.controller.response.ProfileResponse(
            u.nickname.value,
            u.email,
            u.phoneNumber.value
        )
        from UserEntity u
        where u.id = :userId
    """)
    ProfileResponse findProfileByUserId(@Param("userId") Long userId);
}
