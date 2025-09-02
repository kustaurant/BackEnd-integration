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
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                .select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postId.eq(post.postId)
                        .and(reaction.reaction.eq(ReactionType.LIKE)));
        Expression<Long> dislikeCount = JPAExpressions
                .select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postId.eq(post.postId)
                        .and(reaction.reaction.eq(ReactionType.DISLIKE)));
        NumberExpression<Long> totalLikesExpr =
                Expressions.numberTemplate(Long.class, "({0} - {1})", likeCount, dislikeCount);
        // 전체 댓글 수 계산
        Expression<Long> commentCount = JPAExpressions
                .select(comment.postCommentId.count())
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

    // 2. 게시글 1개의 상세 페이지 조회(~댓글까지)
    public Optional<PostDetailProjection> findPostDetail(Long postId, Long currentUserId) {
        Expression<Long> likeCount = JPAExpressions
                .select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postId.eq(postId).and(reaction.reaction.eq(ReactionType.LIKE)));
        Expression<Long> dislikeCount = JPAExpressions
                .select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postId.eq(postId).and(reaction.reaction.eq(ReactionType.DISLIKE)));
        Expression<Long> commentCount = JPAExpressions
                .select(comment.postCommentId.count())
                .from(comment)
                .where(comment.postId.eq(post.postId).and(comment.status.eq(PostCommentStatus.ACTIVE)));
        Expression<Long> scrapCount = JPAExpressions
                .select(scrap.id.userId.count())
                .from(scrap)
                .where(scrap.id.postId.eq(post.postId));

        Expression<ReactionType> myReaction = currentUserId == null ? Expressions.nullExpression(ReactionType.class)
                : JPAExpressions.select(reaction.reaction)
                .from(reaction)
                .where(reaction.id.postId.eq(post.postId)
                        .and(reaction.id.userId.eq(currentUserId)));
        Expression<Boolean> isScrapped = currentUserId == null ? Expressions.FALSE
                : JPAExpressions.selectOne().from(scrap)
                .where(scrap.id.postId.eq(post.postId).and(scrap.id.userId.eq(currentUserId)))
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

    // 3. 포스트 검색
    public Page<PostListProjection> searchLatest(String keyword, Pageable pageable) {
        // 1) ID만 최신순으로 페이징 (post 단독 + 인덱스 타서 가볍게)
        BooleanExpression cond = buildSearchCond(keyword);

        List<Long> ids = queryFactory
                .select(post.postId)
                .from(post)
                .where(cond)
                .orderBy(post.createdAt.desc(), post.postId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalL = queryFactory
                .select(post.count())
                .from(post)
                .where(cond)
                .fetchOne();
        long total = (totalL == null) ? 0L : totalL;

        if (ids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        // 2) 상세/집계는 ID 집합에 대해서만
        // 2-1) 기본 + 작성자 + 작성자 통계(평가수)
        List<Tuple> basics = queryFactory.select(
                        post.postId,
                        post.postCategory,
                        post.postTitle,
                        post.postBody,
                        user.id,
                        user.nickname.value,
                        userStats.ratedRestCnt,
                        post.createdAt
                )
                .from(post)
                .join(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .where(post.postId.in(ids))
                .fetch();

        // 2-2) 좋아요 수
        NumberExpression<Long> likeCountExpr = reaction.count();
        List<Tuple> likeRows = queryFactory
                .select(reaction.id.postId, likeCountExpr)
                .from(reaction)
                .where(reaction.id.postId.in(ids)
                        .and(reaction.reaction.eq(ReactionType.LIKE)))
                .groupBy(reaction.id.postId)
                .fetch();
        Map<Long, Long> likeMap = likeRows.stream()
                .collect(Collectors.toMap(
                        t -> t.get(reaction.id.postId),
                        t -> Optional.ofNullable(t.get(likeCountExpr)).orElse(0L)
                ));

        // 2-3) 댓글 수
        NumberExpression<Long> commentCountExpr = comment.count();
        List<Tuple> commentRows = queryFactory
                .select(comment.postId, commentCountExpr)
                .from(comment)
                .where(comment.postId.in(ids))
                .groupBy(comment.postId)
                .fetch();
        Map<Long, Long> commentMap = commentRows.stream()
                .collect(Collectors.toMap(
                        t -> t.get(comment.postId),
                        t -> Optional.ofNullable(t.get(commentCountExpr)).orElse(0L)
                ));

        // 2-4) 대표 사진 (ACTIVE만, 한 장 선택: min(url))
        StringExpression minPhotoUrlExpr = photo.photoImgUrl.min();
        List<Tuple> photoRows = queryFactory
                .select(photo.postId, minPhotoUrlExpr)
                .from(photo)
                .where(photo.postId.in(ids)
                        .and(photo.status.eq(PostStatus.ACTIVE)))
                .groupBy(photo.postId)
                .fetch();
        Map<Long, String> photoMap = photoRows.stream()
                .collect(Collectors.toMap(
                        t -> t.get(photo.postId),
                        t -> t.get(minPhotoUrlExpr)
                ));

        // 3) 원래 ID 순서로 결과 조립
        Map<Long, Integer> orderIndex = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) orderIndex.put(ids.get(i), i);

        PostListProjection[] bucket = new PostListProjection[ids.size()];

        for (Tuple b : basics) {
            Long pid = b.get(post.postId);
            int pos = orderIndex.get(pid);

            PostCategory category = b.get(post.postCategory);
            String title = b.get(post.postTitle);
            String body = b.get(post.postBody);

            Long writerId = b.get(user.id);
            String writerNickName = b.get(user.nickname.value);
            Number writerEvalCntNum = b.get(userStats.ratedRestCnt);
            long writerEvalCount = (writerEvalCntNum == null) ? 0L : writerEvalCntNum.longValue();

            LocalDateTime createdAt = b.get(post.createdAt);

            long totalLikes = likeMap.getOrDefault(pid, 0L);
            long commentCount = commentMap.getOrDefault(pid, 0L);
            String photoUrl = photoMap.get(pid);

            bucket[pos] = new PostListProjection(
                    pid,
                    category,
                    title,
                    body,
                    writerId,
                    writerNickName,
                    writerEvalCount,
                    photoUrl,
                    createdAt,
                    totalLikes,
                    commentCount
            );
        }
        List<PostListProjection> ordered = new ArrayList<>(bucket.length);
        for (PostListProjection p : bucket) if (p != null) ordered.add(p);

        return new PageImpl<>(ordered, pageable, total);
    }

    private BooleanExpression buildSearchCond(String keyword) {
        BooleanExpression cond = post.status.eq(PostStatus.ACTIVE);
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            cond = cond.and(
                    post.postTitle.containsIgnoreCase(kw)
                            .or(post.postBody.containsIgnoreCase(kw))
            );
        }
        return cond;
    }
}
