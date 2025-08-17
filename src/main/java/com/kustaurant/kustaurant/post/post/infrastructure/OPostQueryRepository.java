package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentEntity;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.OPostDTOProjection;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostScrapEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OPostQueryRepository {

    private final JPAQueryFactory queryFactory;

    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostUserReactionEntity postUserReaction = QPostUserReactionEntity.postUserReactionEntity;
    private static final QPostPhotoEntity postPhoto = QPostPhotoEntity.postPhotoEntity;
    private static final QPostScrapEntity postScrap = QPostScrapEntity.postScrapEntity;
    private static final QPostCommentEntity postComment = QPostCommentEntity.postCommentEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;


    // 검색 키워드로 게시글 목록 조회
    public Page<OPostDTOProjection> findPostsBySearchKeywordWithAllData(
            String keyword, PostCategory category, Pageable pageable, Long currentUserId
    ) {
        QPostUserReactionEntity userReaction = new QPostUserReactionEntity("userReaction");
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");

        // 데이터 조회
        List<OPostDTOProjection> results = queryFactory
                .select(createPostProjectionWithUserStatus(currentUserId))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postUserReaction).on(post.postId.eq(postUserReaction.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(PostStatus.ACTIVE)))
                .leftJoin(userReaction).on(post.postId.eq(userReaction.postId).and(
                        currentUserId != null ? userReaction.userId.eq(currentUserId) : userReaction.userId.isNull()))
                .leftJoin(userScrap).on(post.postId.eq(userScrap.postId).and(
                        currentUserId != null ? userScrap.userId.eq(currentUserId) : userScrap.userId.isNull()))
                .where(buildSearchConditions(keyword, category))
                .groupBy(post.postId, post.postTitle, post.postBody, post.postCategory, post.status,
                        post.createdAt, post.updatedAt, post.postVisitCount, post.userId,
                        user.nickname.value, userStats.ratedRestCnt)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        Long totalCount = queryFactory
                .select(post.countDistinct())
                .from(post)
                .where(buildSearchConditions(keyword, category))
                .fetchOne();

        long count = totalCount != null ? totalCount : 0L;
        return new PageImpl<>(results, pageable, count);
    }

    // 검색 키워드로 인기 게시글 목록 조회
    public Page<OPostDTOProjection> findPopularPostsBySearchKeywordWithAllData(
            String keyword, PostCategory category, Pageable pageable, Long currentUserId, int minLikeCount
    ) {
        QPostUserReactionEntity userReaction = new QPostUserReactionEntity("userReaction");
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");

        // 데이터 조회
        List<OPostDTOProjection> results = queryFactory
                .select(createPostProjectionWithUserStatus(currentUserId))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postUserReaction).on(post.postId.eq(postUserReaction.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(PostStatus.ACTIVE)))
                .leftJoin(userReaction).on(post.postId.eq(userReaction.postId).and(
                        currentUserId != null ? userReaction.userId.eq(currentUserId) : userReaction.userId.isNull()))
                .leftJoin(userScrap).on(post.postId.eq(userScrap.postId).and(
                        currentUserId != null ? userScrap.userId.eq(currentUserId) : userScrap.userId.isNull()))
                .where(buildSearchConditions(keyword, category))
                .groupBy(post.postId, post.postTitle, post.postBody, post.postCategory, post.status,
                        post.createdAt, post.updatedAt, post.postVisitCount, post.userId,
                        user.nickname.value, userStats.ratedRestCnt)
                .having(postUserReaction.postId.count().when(postUserReaction.reaction.eq(ReactionType.LIKE))
                        .otherwise(0L).coalesce(0L).goe(minLikeCount))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회
        Long totalCount = (long) queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(postUserReaction).on(post.postId.eq(postUserReaction.postId))
                .where(buildSearchConditions(keyword, category))
                .groupBy(post.postId)
                .having(postUserReaction.postId.count().when(postUserReaction.reaction.eq(ReactionType.LIKE))
                        .otherwise(0L).coalesce(0L).goe(minLikeCount))
                .fetch().size();

        return new PageImpl<>(results, pageable, totalCount);
    }

    // 검색 조건 구성 헬퍼 메서드
    private BooleanExpression buildSearchConditions(String keyword, PostCategory category) {
        BooleanExpression condition = post.status.eq(PostStatus.ACTIVE);

        // 키워드 검색 조건 추가
        if (keyword != null && !keyword.trim().isEmpty()) {
            condition = condition.and(
                post.postTitle.containsIgnoreCase(keyword)
                .or(post.postBody.containsIgnoreCase(keyword))
            );
        }

        // 카테고리 조건 추가 ("전체"가 아닌 경우에만 필터링)
        if (category != null && category != PostCategory.ALL) {
            condition = condition.and(post.postCategory.eq(category));
        }

        return condition;
    }
}