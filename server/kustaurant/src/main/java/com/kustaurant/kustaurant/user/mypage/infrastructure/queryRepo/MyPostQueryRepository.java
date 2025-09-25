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
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyPostQueryRepository {
    private final JPAQueryFactory factory;

    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostCommentEntity comment = QPostCommentEntity.postCommentEntity;
    private static final QPostScrapEntity scrap = QPostScrapEntity.postScrapEntity;

    private Expression<String> coverPhotoExprForPost() {
        QPostPhotoEntity p1 = new QPostPhotoEntity("p1"); // 서브쿼리1: 최소 photoId
        QPostPhotoEntity p2 = new QPostPhotoEntity("p2"); // 서브쿼리2: 그 photoId의 URL

        // 해당 post의 가장 이른(photoId 최소) 사진 id
        var minPhotoIdExpr = JPAExpressions
                .select(p1.photoId.min())
                .from(p1)
                .where(p1.postId.eq(post.postId));

        // 그 행의 URL 반환
        return JPAExpressions
                .select(p2.photoImgUrl)
                .from(p2)
                .where(p2.postId.eq(post.postId)
                        .and(p2.photoId.eq(minPhotoIdExpr)));
    }

    private Expression<Long> postLikeCountExpr() {
        QPostReactionEntity r2 = new QPostReactionEntity("r2");
        return JPAExpressions
                .select(r2.count())
                .from(r2)
                .where(r2.id.postId.eq(post.postId)
                        .and(r2.reaction.eq(ReactionType.LIKE)));
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
        return JPAExpressions
                .select(cr2.count())
                .from(cr2)
                .where(cr2.id.postCommentId.eq(c.postCommentId)
                        .and(cr2.reaction.eq(ReactionType.LIKE)));
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
                .join(post).on(post.postId.eq(scrap.id.postId))
                .where(scrap.id.userId.eq(userId)
                        .and(post.status.eq(PostStatus.ACTIVE)))
                .orderBy(scrap.createdAt.desc())
                .fetch();
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<MyPostCommentResponse> findMyPostCommentsByUserId(Long userId) {
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
