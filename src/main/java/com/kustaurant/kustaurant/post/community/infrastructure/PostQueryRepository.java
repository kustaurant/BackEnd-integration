package com.kustaurant.kustaurant.post.community.infrastructure;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.common.enums.SortOption;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentEntity;
import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostScrapEntity;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostDetailProjection;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostListProjection;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostPhotoEntity photo = QPostPhotoEntity.postPhotoEntity;
    private static final QPostReactionEntity reaction = QPostReactionEntity.postReactionEntity;
    private static final QPostCommentEntity comment = QPostCommentEntity.postCommentEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;
    private static final QPostScrapEntity scrap = QPostScrapEntity.postScrapEntity;

    private final QPostPhotoEntity thumbnailPhoto = new QPostPhotoEntity("ph2");

    // 1. 게시글 목록 페이징(기본값 10개) 나열
    public Page<PostListProjection> findPostList(PostCategory category, SortOption sort, Pageable pageable) {
        // 전체 좋아요 수 계산
        Expression<Long> likeCount = JPAExpressions
                .select(reaction.userId.count())
                .from(reaction)
                .where(reaction.postId.eq(post.postId)
                        .and(reaction.reaction.eq(ReactionType.LIKE)));
        Expression<Long> dislikeCount = JPAExpressions
                .select(reaction.userId.count())
                .from(reaction)
                .where(reaction.postId.eq(post.postId)
                        .and(reaction.reaction.eq(ReactionType.DISLIKE)));
        NumberExpression<Long> totalLikesExpr =
                Expressions.numberTemplate(Long.class, "({0} - {1})", likeCount, dislikeCount);
        // 전체 댓글 수 계산
        Expression<Long> commentCount = JPAExpressions
                .select(comment.commentId.count())
                .from(comment)
                .where(comment.postId.eq(post.postId)
                        .and(comment.status.ne(PostCommentStatus.DELETED)));
        // 사진 선택
        Expression<Integer> thumbnailPhotoId = JPAExpressions
                .select(thumbnailPhoto.photoId.min())
                .from(thumbnailPhoto)
                .where(thumbnailPhoto.postId.eq(post.postId)
                        .and(thumbnailPhoto.status.eq(PostStatus.ACTIVE)));
        // 게시글 카테고리
        BooleanBuilder where = new BooleanBuilder().and(post.status.eq(PostStatus.ACTIVE));
        if (category != null && category != PostCategory.ALL) {
            where.and(post.postCategory.eq(category));
        }
        // 정렬순서 ( 최신순/인기순 )
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == SortOption.POPULARITY) {
            orders.add(totalLikesExpr.desc());
            orders.add(post.createdAt.desc());
        } else {
            orders.add(post.createdAt.desc());
        }
        // 필요한것만 추출
        List<PostListProjection> projections = queryFactory
                .select(Projections.constructor(
                        PostListProjection.class,
                        post.postId,
                        post.postCategory,
                        post.postTitle,
                        post.postBody,
                        post.userId,
                        user.nickname.value,
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", userStats.ratedRestCnt),
                        photo.photoImgUrl,
                        post.createdAt,
                        Expressions.numberTemplate(Long.class, "COALESCE({0}, 0)", totalLikesExpr),
                        Expressions.numberTemplate(Long.class, "COALESCE({0}, 0)", commentCount)
                ))
                .from(post)
                .leftJoin(user).on(user.id.eq(post.userId))
                .leftJoin(userStats).on(userStats.id.eq(user.id))
                .leftJoin(photo).on(photo.postId.eq(post.postId)
                        .and(photo.status.eq(PostStatus.ACTIVE))
                        .and(photo.photoId.eq(thumbnailPhotoId))
                )
                .where(where)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // ===== count =====
        JPAQuery<Long> countQuery = queryFactory
                .select(post.postId.count())
                .from(post)
                .where(where);

        return PageableExecutionUtils.getPage(projections, pageable, countQuery::fetchOne);
    }

    // 2. 게시글 1개의 상세 페이지 조회
    public Optional<PostDetailProjection> findPostDetail(Integer postId, Long currentUserId) {
        Expression<Long> likeCount = JPAExpressions
                .select(reaction.userId.count())
                .from(reaction)
                .where(reaction.postId.eq(postId).and(reaction.reaction.eq(ReactionType.LIKE)));
        Expression<Long> dislikeCount = JPAExpressions
                .select(reaction.userId.count())
                .from(reaction)
                .where(reaction.postId.eq(postId).and(reaction.reaction.eq(ReactionType.DISLIKE)));
        Expression<Long> commentCount = JPAExpressions
                .select(comment.commentId.count())
                .from(comment)
                .where(comment.postId.eq(post.postId).and(comment.status.eq(PostCommentStatus.ACTIVE)));
        Expression<Long> scrapCount = JPAExpressions
                .select(scrap.userId.count())
                .from(scrap)
                .where(scrap.postId.eq(post.postId));

        Expression<ReactionType> myReaction = currentUserId == null ? Expressions.nullExpression()
                : JPAExpressions.select(reaction.reaction)
                .from(reaction)
                .where(reaction.postId.eq(post.postId)
                        .and(reaction.userId.eq(currentUserId)));
        Expression<Boolean> isScrapped = currentUserId == null ? Expressions.FALSE
                : JPAExpressions.selectOne().from(scrap)
                .where(scrap.postId.eq(post.postId).and(scrap.userId.eq(currentUserId)))
                .exists();

        PostDetailProjection projection = queryFactory
                .select(Projections.constructor(
                        PostDetailProjection.class,
                        post.postId,
                        post.postCategory,
                        post.postTitle,
                        post.postBody,
                        post.userId,
                        user.nickname.value,
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", userStats.ratedRestCnt),
                        post.createdAt,
                        post.updatedAt,
                        post.postVisitCount,
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", likeCount),
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", dislikeCount),
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", commentCount),
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", scrapCount),
                        myReaction,
                        isScrapped
                ))
                .from(post)
                .leftJoin(user).on(user.id.eq(post.userId))
                .leftJoin(userStats).on(userStats.id.eq(user.id))
                .where(post.postId.eq(postId).and(post.status.eq(PostStatus.ACTIVE)))
                .fetchOne();

        return Optional.ofNullable(projection);
    }
}
