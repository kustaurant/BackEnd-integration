package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.QPostCommentReactionEntity;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostScrapEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyPostQueryRepository {
    private final JPAQueryFactory factory;

    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostCommentEntity comment = QPostCommentEntity.postCommentEntity;
    private static final QPostPhotoEntity photo = QPostPhotoEntity.postPhotoEntity;
    private static final QPostScrapEntity scrap = QPostScrapEntity.postScrapEntity;
    private static final QPostReactionEntity postReaction = QPostReactionEntity.postReactionEntity;
    private static final QPostCommentReactionEntity commentReaction = QPostCommentReactionEntity.postCommentReactionEntity;

    private Expression<String> coverPhotoExprForPost() {
        QPostPhotoEntity p2 = new QPostPhotoEntity("p2");
        return JPAExpressions
                .select(p2.photoImgUrl.min())
                .from(p2)
                .where(p2.postId.eq(post.postId));
    }

    private Expression<Long> postLikeCountExpr() {
        QPostReactionEntity r2 = new QPostReactionEntity("r2");
        var likeCase = new CaseBuilder()
                .when(r2.reaction.eq(ReactionType.LIKE)).then(1)
                .otherwise(0);
        return JPAExpressions
                .select(likeCase.sum().longValue())
                .from(r2)
                .where(r2.postId.eq(post.postId));
    }

    private Expression<Long> postCommentCountExpr() {
        QPostCommentEntity c2 = new QPostCommentEntity("c2");
        return JPAExpressions
                .select(c2.count())
                .from(c2)
                .where(c2.postId.eq(post.postId));
    }

    private Expression<Long> commentLikeCountExpr(QPostCommentEntity c) {
        QPostCommentReactionEntity cr2 = new QPostCommentReactionEntity("cr2");
        var likeCase = new CaseBuilder()
                .when(cr2.reaction.eq(ReactionType.LIKE)).then(1)
                .otherwise(0);
        return JPAExpressions
                .select(likeCase.sum().longValue())
                .from(cr2)
                .where(cr2.postCommentId.eq(c.commentId));
    }
    //-----
    public List<MyPostsResponse> findMyPostsByUserId(Long userId) {
        return factory.select(Projections.constructor(
                MyPostsResponse.class,
                post.postId,
                post.postCategory,
                post.postTitle,
                coverPhotoExprForPost(),
                post.postBody,
                postLikeCountExpr(),
                postCommentCountExpr(),
                post.createdAt
        ))
                .from(post)
                .where(post.userId.eq(userId)
                        .and(post.status.eq(PostStatus.ACTIVE)))
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    public List<MyPostsResponse> findMyScrappedPostsByUserId(Long userId) {
        return factory.select(Projections.constructor(
                        MyPostsResponse.class,
                        post.postId,
                        post.postCategory,
                        post.postTitle,
                        coverPhotoExprForPost(),
                        post.postBody,
                        postLikeCountExpr(),
                        postCommentCountExpr(),
                        post.createdAt
                ))
                .from(scrap)
                .join(post).on(post.postId.eq(scrap.postId))
                .where(scrap.userId.eq(userId)
                        .and(post.status.eq(PostStatus.ACTIVE)))
                .orderBy(scrap.createdAt.desc())
                .fetch();
    }

    public List<MyPostCommentResponse> findCommentsByUserId(Long userId) {
        return factory.select(Projections.constructor(
                MyPostCommentResponse.class,
                post.postId,
                post.postCategory,
                post.postTitle,
                comment.commentBody,
                commentLikeCountExpr(comment),
                comment.createdAt
                ))
                .from(comment)
                .join(post).on(post.postId.eq(comment.postId))
                .where(comment.userId.eq(userId)
                        .and(post.status.eq(PostStatus.ACTIVE)))
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
