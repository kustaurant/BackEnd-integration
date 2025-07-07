package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostLikeEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostDislikeEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostPhotoEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.QPostScrapEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.QPostCommentEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.QUserEntity;
import com.kustaurant.kustaurant.user.mypage.infrastructure.QUserStatsEntity;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostQueryDAOImpl implements PostQueryDAO {

    private final JPAQueryFactory queryFactory;
    
    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostLikeEntity postLike = QPostLikeEntity.postLikeEntity;
    private static final QPostDislikeEntity postDislike = QPostDislikeEntity.postDislikeEntity;
    private static final QPostPhotoEntity postPhoto = QPostPhotoEntity.postPhotoEntity;
    private static final QPostScrapEntity postScrap = QPostScrapEntity.postScrapEntity;
    private static final QPostCommentEntity postComment = QPostCommentEntity.postCommentEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;

    // 단일 게시글 조회
    @Override
    public Optional<PostDTOProjection> findPostWithAllData(Integer postId, Long currentUserId) {
        PostDTOProjection result = queryFactory
                .select(createPostProjection(currentUserId))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postLike).on(post.postId.eq(postLike.postId))
                .leftJoin(postDislike).on(post.postId.eq(postDislike.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(ContentStatus.ACTIVE)))
                .where(post.postId.eq(postId)
                        .and(post.status.eq(ContentStatus.ACTIVE)))
                .groupBy(post.postId, post.postTitle, post.postBody, post.postCategory, post.status, 
                        post.createdAt, post.updatedAt, post.postVisitCount, post.userId, 
                        user.nickname.value, userStats.ratedRestCnt)
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
    
    private ConstructorExpression<PostDTOProjection> createPostProjection(Long currentUserId) {
        return Projections.constructor(PostDTOProjection.class,
                post.postId,
                post.postTitle,
                post.postBody,
                post.postCategory,
                post.status.stringValue(),
                post.createdAt,
                post.updatedAt,
                post.postVisitCount,
                post.userId,
                user.nickname.value,
                userStats.ratedRestCnt.coalesce(0),
                postLike.postLikesId.countDistinct().coalesce(0L),
                postDislike.postDislikesId.countDistinct().coalesce(0L),
                postComment.commentId.countDistinct().coalesce(0L),
                postScrap.scrapId.countDistinct().coalesce(0L),
                postPhoto.photoImgUrl.min().coalesce(""),
                Expressions.asBoolean(false),
                Expressions.asBoolean(false)
        );
    }


    // 게시글 페이지로 조회
    @Override
    public Page<PostDTOProjection> findPostsWithAllData(Pageable pageable, Long currentUserId) {
        QPostLikeEntity userLike = new QPostLikeEntity("userLike");
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");
        
        // 데이터 조회
        List<PostDTOProjection> results = queryFactory
                .select(createPostProjectionWithUserStatus(currentUserId))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postLike).on(post.postId.eq(postLike.postId))
                .leftJoin(postDislike).on(post.postId.eq(postDislike.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(ContentStatus.ACTIVE)))
                .leftJoin(userLike).on(post.postId.eq(userLike.postId).and(
                        currentUserId != null ? userLike.userId.eq(currentUserId) : userLike.userId.isNull()))
                .leftJoin(userScrap).on(post.postId.eq(userScrap.postId).and(
                        currentUserId != null ? userScrap.userId.eq(currentUserId) : userScrap.userId.isNull()))
                .where(post.status.eq(ContentStatus.ACTIVE))
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
                .where(post.status.eq(ContentStatus.ACTIVE))
                .fetchOne();
        
        long count = totalCount != null ? totalCount : 0L;
        
        return new PageImpl<>(results, pageable, count);
    }

    @Override
    public Page<PostDTOProjection> findPostsByCategoryWithAllData(String category, Pageable pageable, Long currentUserId) {
        QPostLikeEntity userLike = new QPostLikeEntity("userLike");
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");
        
        // 데이터 조회
        List<PostDTOProjection> results = queryFactory
                .select(createPostProjectionWithUserStatus(currentUserId))
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postLike).on(post.postId.eq(postLike.postId))
                .leftJoin(postDislike).on(post.postId.eq(postDislike.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(ContentStatus.ACTIVE)))
                .leftJoin(userLike).on(post.postId.eq(userLike.postId).and(
                        currentUserId != null ? userLike.userId.eq(currentUserId) : userLike.userId.isNull()))
                .leftJoin(userScrap).on(post.postId.eq(userScrap.postId).and(
                        currentUserId != null ? userScrap.userId.eq(currentUserId) : userScrap.userId.isNull()))
                .where(post.status.eq(ContentStatus.ACTIVE)
                        .and(post.postCategory.eq(category)))
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
                .where(post.status.eq(ContentStatus.ACTIVE)
                        .and(post.postCategory.eq(category)))
                .fetchOne();
        
        long count = totalCount != null ? totalCount : 0L;
        
        return new PageImpl<>(results, pageable, count);
    }

    private ConstructorExpression<PostDTOProjection> createPostProjectionWithUserStatus(Long currentUserId) {
        QPostLikeEntity userLike = new QPostLikeEntity("userLike");
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");

        return Projections.constructor(PostDTOProjection.class,
                post.postId,
                post.postTitle,
                post.postBody,
                post.postCategory,
                post.status.stringValue(),
                post.createdAt,
                post.updatedAt,
                post.postVisitCount,
                post.userId,
                user.nickname.value,
                userStats.ratedRestCnt.coalesce(0),
                postLike.postLikesId.countDistinct().coalesce(0L),
                postDislike.postDislikesId.countDistinct().coalesce(0L),
                postComment.commentId.countDistinct().coalesce(0L),
                postScrap.scrapId.countDistinct().coalesce(0L),
                postPhoto.photoImgUrl.min().coalesce(""),
                currentUserId != null ?
                        Expressions.cases()
                                .when(userLike.userId.max().isNotNull())
                                .then(true)
                                .otherwise(false) :
                        Expressions.asBoolean(false),
                currentUserId != null ?
                        Expressions.cases()
                                .when(userScrap.userId.max().isNotNull())
                                .then(true)
                                .otherwise(false) :
                        Expressions.asBoolean(false)
        );
    }



    // 마이페이지 내가 작성한 글 조회
    @Override
    public List<PostDTOProjection> findMyWrittenPosts(Long currentUserId) {
        return queryFactory
                .select(createMyPageProjection())
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postLike).on(post.postId.eq(postLike.postId))
                .leftJoin(postDislike).on(post.postId.eq(postDislike.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(ContentStatus.ACTIVE)))
                .where(post.status.eq(ContentStatus.ACTIVE)
                        .and(post.userId.eq(currentUserId)))
                .groupBy(post.postId, post.postTitle, post.postBody, post.postCategory, post.status, 
                        post.createdAt, post.updatedAt, post.postVisitCount, post.userId, 
                        user.nickname.value, userStats.ratedRestCnt)
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    // 마이페이지 내가 스크랩한 글 조회
    @Override
    public List<PostDTOProjection> findMyScrappedPosts(Long currentUserId) {
        QPostScrapEntity userScrap = new QPostScrapEntity("userScrap");
        
        return queryFactory
                .select(createMyPageProjection())
                .from(post)
                .leftJoin(user).on(post.userId.eq(user.id))
                .leftJoin(userStats).on(user.id.eq(userStats.id))
                .leftJoin(postLike).on(post.postId.eq(postLike.postId))
                .leftJoin(postDislike).on(post.postId.eq(postDislike.postId))
                .leftJoin(postComment).on(post.postId.eq(postComment.postId))
                .leftJoin(postScrap).on(post.postId.eq(postScrap.postId))
                .leftJoin(postPhoto).on(post.postId.eq(postPhoto.postId).and(postPhoto.status.eq(ContentStatus.ACTIVE)))
                .where(post.status.eq(ContentStatus.ACTIVE)
                        .and(queryFactory.selectOne()
                                .from(userScrap)
                                .where(userScrap.postId.eq(post.postId)
                                        .and(userScrap.userId.eq(currentUserId)))
                                .exists()))
                .groupBy(post.postId, post.postTitle, post.postBody, post.postCategory, post.status, 
                        post.createdAt, post.updatedAt, post.postVisitCount, post.userId, 
                        user.nickname.value, userStats.ratedRestCnt)
                .orderBy(post.createdAt.desc())
                .fetch();
    }

    // 마이페이지 전용 게시글 조회 쿼리
    private ConstructorExpression<PostDTOProjection> createMyPageProjection() {
        return Projections.constructor(PostDTOProjection.class,
                post.postId,
                post.postTitle,
                post.postBody,
                post.postCategory,
                post.status.stringValue(),
                post.createdAt,
                post.updatedAt,
                post.postVisitCount,
                post.userId,
                user.nickname.value,
                userStats.ratedRestCnt.coalesce(0),
                postLike.postLikesId.countDistinct().coalesce(0L),
                postDislike.postDislikesId.countDistinct().coalesce(0L),
                postComment.commentId.countDistinct().coalesce(0L),
                postScrap.scrapId.countDistinct().coalesce(0L),
                postPhoto.photoImgUrl.min().coalesce(""),
                Expressions.asBoolean(false), // isLiked - 마이페이지에서는 불필요
                Expressions.asBoolean(false)  // isScraped - 마이페이지에서는 불필요
        );
    }

}