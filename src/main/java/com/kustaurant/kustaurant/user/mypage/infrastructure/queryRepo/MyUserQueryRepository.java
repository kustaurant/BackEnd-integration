package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.user.mypage.controller.response.api.ProfileResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.UserActivityStatsResponse;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.user.mypage.infrastructure.projection.MypageMainProjection;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MyUserQueryRepository {
    private final JPAQueryFactory factory;
    private final QUserEntity u = QUserEntity.userEntity;
    private final QUserStatsEntity s = QUserStatsEntity.userStatsEntity;

    public MypageMainProjection findMypageMainByUserId(Long userId) {
        return factory.select(Projections.constructor(
                        MypageMainProjection.class,
                        u.nickname.value,
                        s.ratedRestCnt,
                        s.commPostCnt))
                .from(u)
                .leftJoin(s).on(s.id.eq(u.id))
                .where(u.id.eq(userId))
                .fetchOne();

    };

    public ProfileResponse findProfileByUserId(Long userId) {
        return factory.select(Projections.constructor(
                        ProfileResponse.class,
                        u.nickname.value,
                        u.email,
                        u.phoneNumber.value))
                .from(u)
                .where(u.id.eq(userId))
                .fetchOne();
    };

    public UserActivityStatsResponse findStatsByUserId(Long userId) {
        return factory.select(Projections.constructor(
                        UserActivityStatsResponse.class,
                        s.savedRestCnt,
                        s.ratedRestCnt,
                        s.commPostCnt,
                        s.commCommentCnt,
                        s.commSavedPostCnt))
                .from(s)
                .where(s.id.eq(userId))
                .fetchOne();
    };
}
