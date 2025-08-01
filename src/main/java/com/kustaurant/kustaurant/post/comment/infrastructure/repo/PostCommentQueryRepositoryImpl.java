package com.kustaurant.kustaurant.post.comment.infrastructure.repo;

import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentDislikeEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentLikeEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.repo.projection.PostCommentDetailProjection;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCommentQueryRepositoryImpl implements PostCommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    
    private static final QPostCommentEntity postComment = QPostCommentEntity.postCommentEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;
    private static final QPostCommentLikeEntity commentLike = QPostCommentLikeEntity.postCommentLikeEntity;
    private static final QPostCommentDislikeEntity commentDislike = QPostCommentDislikeEntity.postCommentDislikeEntity;

    
    @Override
    public List<PostCommentDetailProjection> findCommentTreeByPostId(Integer postId, Long currentUserId, String sort) {
        QPostCommentLikeEntity userLike = new QPostCommentLikeEntity("userLike");
        QPostCommentDislikeEntity userDislike = new QPostCommentDislikeEntity("userDislike");
        
        return queryFactory
                .select(Projections.constructor(PostCommentDetailProjection.class,
                        postComment.commentId,
                        postComment.commentBody,
                        postComment.status.stringValue(),
                        postComment.parentCommentId,
                        postComment.createdAt,
                        postComment.updatedAt,
                        postComment.postId,
                        postComment.userId,
                        user.nickname.value,
                        userStats.ratedRestCnt.coalesce(0),
                        commentLike.commentLikeId.countDistinct().coalesce(0L),
                        commentDislike.commentDislikeId.countDistinct().coalesce(0L),
                        currentUserId != null ?
                                Expressions.cases()
                                        .when(userLike.userId.max().isNotNull())
                                        .then(true)
                                        .otherwise(false) :
                                Expressions.asBoolean(false),
                        currentUserId != null ?
                                Expressions.cases()
                                        .when(userDislike.userId.max().isNotNull())
                                        .then(true)
                                        .otherwise(false) :
                                Expressions.asBoolean(false),
                        currentUserId != null ?
                                Expressions.cases()
                                        .when(postComment.userId.eq(currentUserId))
                                        .then(true)
                                        .otherwise(false) :
                                Expressions.asBoolean(false)
                ))
                .from(postComment)
                .leftJoin(user).on(postComment.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(commentLike).on(postComment.commentId.eq(commentLike.commentId))
                .leftJoin(commentDislike).on(postComment.commentId.eq(commentDislike.commentId))
                .leftJoin(userLike).on(postComment.commentId.eq(userLike.commentId).and(
                        currentUserId != null ? userLike.userId.eq(currentUserId) : userLike.userId.isNull()))
                .leftJoin(userDislike).on(postComment.commentId.eq(userDislike.commentId).and(
                        currentUserId != null ? userDislike.userId.eq(currentUserId) : userDislike.userId.isNull()))
                .where(postComment.postId.eq(postId)
                        .and(postComment.status.eq(ContentStatus.ACTIVE)))
                .groupBy(postComment.commentId, postComment.commentBody, postComment.status,
                        postComment.parentCommentId, postComment.createdAt, postComment.updatedAt,
                        postComment.postId, postComment.userId, user.nickname.value, userStats.ratedRestCnt)
                .orderBy(getCommentOrderSpecifier(sort))
                .fetch();
    }
    
    private OrderSpecifier<?> getCommentOrderSpecifier(String sort) {
        if ("popular".equals(sort)) {
            return new OrderSpecifier<>(Order.DESC, commentLike.commentLikeId.countDistinct().subtract(commentDislike.commentDislikeId.countDistinct()));
        } else {
            return new OrderSpecifier<>(Order.DESC, postComment.createdAt);
        }
    }
}