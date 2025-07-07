package com.kustaurant.kustaurant.post.comment.infrastructure;

import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryDAO;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentLikeEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentDislikeEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCommentQueryDAOImpl implements PostCommentQueryDAO {

    private final JPAQueryFactory queryFactory;
    
    private static final QPostCommentEntity postComment = QPostCommentEntity.postCommentEntity;
    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;
    private static final QPostCommentLikeEntity commentLike = QPostCommentLikeEntity.postCommentLikeEntity;
    private static final QPostCommentDislikeEntity commentDislike = QPostCommentDislikeEntity.postCommentDislikeEntity;

    @Override
    public List<PostCommentDTOProjection> findMyCommentedPostsWithDetails(Long currentUserId) {
        return queryFactory
                .select(Projections.constructor(PostCommentDTOProjection.class,
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
                        commentLike.commentLikeId.count().coalesce(0L),
                        commentDislike.commentDislikeId.count().coalesce(0L),
                        post.postTitle,
                        post.postCategory
                ))
                .from(postComment)
                .leftJoin(user).on(postComment.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(post).on(postComment.postId.eq(post.postId))
                .leftJoin(commentLike).on(postComment.commentId.eq(commentLike.commentId))
                .leftJoin(commentDislike).on(postComment.commentId.eq(commentDislike.commentId))
                .where(postComment.userId.eq(currentUserId)
                        .and(postComment.status.eq(ContentStatus.ACTIVE))
                        .and(post.status.eq(ContentStatus.ACTIVE)))
                .groupBy(postComment.commentId, postComment.commentBody, postComment.status, 
                        postComment.parentCommentId, postComment.createdAt, postComment.updatedAt, 
                        postComment.postId, postComment.userId, user.nickname.value, 
                        userStats.ratedRestCnt, post.postTitle, post.postCategory)
                .orderBy(postComment.createdAt.desc())
                .fetch();
    }
}