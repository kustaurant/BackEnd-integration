package com.kustaurant.kustaurant.post.community.infrastructure;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentReactionEntity;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostCommentProjection;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {
    private final JPAQueryFactory queryFactory;
    private static final QPostCommentEntity comment = QPostCommentEntity.postCommentEntity;
    private static final QPostCommentReactionEntity reaction = QPostCommentReactionEntity.postCommentReactionEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<PostCommentProjection> findComments(Long postId, Long currentUserId) {
        Expression<Long> likeCount = JPAExpressions.select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postCommentId.eq(comment.postCommentId)
                        .and(reaction.reaction.eq(ReactionType.LIKE))
                );
        Expression<Long> dislikeCount = JPAExpressions.select(reaction.id.userId.count())
                .from(reaction)
                .where(reaction.id.postCommentId.eq(comment.postCommentId)
                        .and(reaction.reaction.eq(ReactionType.DISLIKE))
                );
        Expression<ReactionType> myReaction = currentUserId == null ? Expressions.nullExpression(ReactionType.class)
                : JPAExpressions.select(reaction.reaction)
                .from(reaction)
                .where(reaction.id.postCommentId.eq(comment.postCommentId)
                        .and(reaction.id.userId.eq(currentUserId)));

        return queryFactory.select(Projections.constructor(
                        PostCommentProjection.class,
                        comment.postCommentId,
                        comment.parentCommentId,
                        comment.commentBody,
                        comment.status,
                        comment.userId,
                        user.nickname.value,
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", userStats.ratedRestCnt),
                        comment.createdAt,
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", likeCount),
                        Expressions.numberTemplate(Long.class, "COALESCE({0},0)", dislikeCount),
                        myReaction
                ))
                .from(comment)
                .leftJoin(user).on(user.id.eq(comment.userId))
                .leftJoin(userStats).on(userStats.id.eq(user.id))
                .where(
                        comment.postId.eq(postId)
                                .and(comment.status.ne(PostCommentStatus.DELETED))
                )
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
