package com.kustaurant.mainapp.admin.adminPage.infrastructure;

import com.kustaurant.mainapp.admin.adminPage.controller.response.AdminStatsResponse;
import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.kustaurant.jpa.restaurant.entity.QRestaurantEntity.restaurantEntity;
import static com.kustaurant.mainapp.post.comment.infrastructure.entity.QPostCommentEntity.postCommentEntity;
import static com.kustaurant.mainapp.admin.report.QReportEntity.reportEntity;
import static com.kustaurant.mainapp.admin.feedback.infrastructure.QFeedbackEntity.feedbackEntity;
import static com.kustaurant.mainapp.post.post.infrastructure.entity.QPostEntity.postEntity;
import static com.kustaurant.mainapp.evaluation.evaluation.infrastructure.entity.QEvaluationEntity.evaluationEntity;
import static com.kustaurant.mainapp.evaluation.comment.infrastructure.entity.QEvalCommentEntity.evalCommentEntity;
import static com.kustaurant.mainapp.user.user.infrastructure.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class AdminStatsQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AdminStatsResponse getAdminStats() {
        // 각각의 카운트를 병렬로 조회
        Long totalRestaurants = queryFactory
                .select(restaurantEntity.count())
                .from(restaurantEntity)
                .fetchOne();

        Long totalReports = queryFactory
                .select(reportEntity.count())
                .from(reportEntity)
                .fetchOne();

        Long totalFeedback = queryFactory
                .select(feedbackEntity.count())
                .from(feedbackEntity)
                .fetchOne();

        Long totalCommunityPosts = queryFactory
                .select(postEntity.count())
                .from(postEntity)
                .fetchOne();

        Long totalEvaluations = queryFactory
                .select(evaluationEntity.count())
                .from(evaluationEntity)
                .fetchOne();

        Long totalCommunityComments = queryFactory
                .select(postCommentEntity.count())
                .from(postCommentEntity)
                .fetchOne();

        Long totalEvaluationComments = queryFactory
                .select(evalCommentEntity.count())
                .from(evalCommentEntity)
                .fetchOne();

        Long totalNaverUsers = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.loginApi.eq(LoginApi.NAVER))
                .fetchOne();

        Long totalAppleUsers = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .where(userEntity.loginApi.eq(LoginApi.APPLE))
                .fetchOne();

        Long totalUsers = queryFactory
                .select(userEntity.count())
                .from(userEntity)
                .fetchOne();

        return new AdminStatsResponse(
                totalRestaurants != null ? totalRestaurants : 0L,
                totalReports != null ? totalReports : 0L,
                totalFeedback != null ? totalFeedback : 0L,
                totalCommunityPosts != null ? totalCommunityPosts : 0L,
                totalEvaluations != null ? totalEvaluations : 0L,
                totalCommunityComments != null ? totalCommunityComments : 0L,
                totalEvaluationComments != null ? totalEvaluationComments : 0L,
                totalUsers != null ? totalUsers : 0L,
                totalNaverUsers != null ? totalNaverUsers : 0L,
                totalAppleUsers != null ? totalAppleUsers : 0L
        );
    }
}