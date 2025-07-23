package com.kustaurant.kustaurant.user.mypage.infrastructure.queryRepo;

import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentLikeEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostLikeEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostScrapEntity;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostCommentResponse;
import com.kustaurant.kustaurant.user.mypage.controller.response.api.MyPostsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
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
    private static final QPostLikeEntity like = QPostLikeEntity.postLikeEntity;
    private static final QPostCommentEntity comment = QPostCommentEntity.postCommentEntity;
    private static final QPostPhotoEntity photo = QPostPhotoEntity.postPhotoEntity;
    private static final QPostScrapEntity scrap = QPostScrapEntity.postScrapEntity;
    private static final QPostCommentLikeEntity commentLike = QPostCommentLikeEntity.postCommentLikeEntity;

    public List<MyPostsResponse> findMyPostsByUserId(Long userId) {
        return factory.select(Projections.constructor(
                MyPostsResponse.class,
                post.postId,
                post.postCategory,
                post.postTitle,
                photo.photoImgUrl.min(),
                post.postBody,
                like.postLikesId.countDistinct(),
                comment.commentId.countDistinct(),
                post.createdAt
        ))
                .from(post)
                .leftJoin(like).on(like.postId.eq(post.postId))
                .leftJoin(comment).on(comment.postId.eq(post.postId))
                .leftJoin(photo).on(photo.postId.eq(post.postId))
                .where(post.userId.eq(userId))
                .groupBy(post.postId)
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    public List<MyPostsResponse> findMyScrappedPostsByUserId(Long userId) {
        // “첫 번째 사진 1장” 가져올 서브쿼리용 별칭
        QPostPhotoEntity subPhoto = new QPostPhotoEntity("subPhoto");

        return factory.select(Projections.constructor(
                        MyPostsResponse.class,
                        post.postId,
                        post.postCategory,
                        post.postTitle,
                        photo.photoImgUrl.min(),
                        post.postBody,
                        like.postLikesId.countDistinct(),
                        comment.commentId.countDistinct(),
                        post.createdAt
                ))
                .from(scrap)
                .join(post).on(post.postId.eq(scrap.postId))
                .leftJoin(like).on(like.postId.eq(post.postId))
                .leftJoin(comment).on(comment.postId.eq(post.postId))
                .leftJoin(photo).on(photo.postId.eq(post.postId))
                .where(scrap.userId.eq(userId))
                .groupBy(post.postId)
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
                commentLike.commentLikeId.countDistinct(),
                comment.createdAt
                ))
                .from(comment)
                .join(post).on(post.postId.eq(comment.postId))
                .leftJoin(commentLike).on(commentLike.commentId.eq(comment.commentId))
                .where(comment.userId.eq(userId))
                .groupBy(comment.commentId, post.postId, post.postCategory, post.postTitle, comment.commentBody, comment.createdAt)
                .orderBy(comment.createdAt.desc())
                .fetch();
    }
}
