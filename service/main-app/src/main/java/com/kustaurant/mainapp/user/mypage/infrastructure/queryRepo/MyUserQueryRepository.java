package com.kustaurant.mainapp.user.mypage.infrastructure.queryRepo;

import com.kustaurant.mainapp.user.mypage.controller.response.api.ProfileResponse;
import com.kustaurant.mainapp.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.mainapp.user.user.infrastructure.QUserEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MyUserQueryRepository {
    private final JPAQueryFactory factory;
    private final QUserEntity u = QUserEntity.userEntity;
    private final QUserStatsEntity s = QUserStatsEntity.userStatsEntity;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ProfileResponse getProfile(Long userId) {
        return factory.select(Projections.constructor(
                ProfileResponse.class,
                u.nickname.value,
                s.savedRestCnt,
                s.ratedRestCnt,
                s.commPostCnt,
                s.commCommentCnt,
                s.commSavedPostCnt,
                u.email,
                u.phoneNumber.value))
                .from(u)
                .leftJoin(s).on(s.id.eq(u.id))
                .where(u.id.eq(userId))
                .fetchOne();
    }
}
