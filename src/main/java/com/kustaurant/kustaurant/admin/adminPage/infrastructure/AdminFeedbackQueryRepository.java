package com.kustaurant.kustaurant.admin.adminPage.infrastructure;

import com.kustaurant.kustaurant.admin.adminPage.dto.FeedbackListResponse;
import com.kustaurant.kustaurant.admin.adminPage.dto.PagedFeedbackResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kustaurant.kustaurant.admin.feedback.infrastructure.QFeedbackEntity.feedbackEntity;
import static com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class AdminFeedbackQueryRepository {

    private final JPAQueryFactory queryFactory;

    public PagedFeedbackResponse getAllFeedbacks(Pageable pageable) {
        List<FeedbackListResponse> feedbacks = queryFactory
                .select(Projections.constructor(FeedbackListResponse.class,
                        feedbackEntity.id,
                        feedbackEntity.comment,
                        feedbackEntity.userId,
                        userEntity.nickname.value,
                        feedbackEntity.createdAt
                ))
                .from(feedbackEntity)
                .leftJoin(userEntity).on(feedbackEntity.userId.eq(userEntity.id))
                .orderBy(feedbackEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElements = queryFactory
                .select(feedbackEntity.count())
                .from(feedbackEntity)
                .fetchOne();

        totalElements = totalElements != null ? totalElements : 0L;
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return new PagedFeedbackResponse(
                feedbacks,
                totalElements,
                totalPages,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getPageNumber() < totalPages - 1,
                pageable.getPageNumber() > 0
        );
    }

    public Long getTotalFeedbacks() {
        Long count = queryFactory
                .select(feedbackEntity.count())
                .from(feedbackEntity)
                .fetchOne();
        
        return count != null ? count : 0L;
    }
}