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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    private final EntityManager entityManager;
    
    private static final QPostEntity post = QPostEntity.postEntity;
    private static final QPostLikeEntity postLike = QPostLikeEntity.postLikeEntity;
    private static final QPostDislikeEntity postDislike = QPostDislikeEntity.postDislikeEntity;
    private static final QPostPhotoEntity postPhoto = QPostPhotoEntity.postPhotoEntity;
    private static final QPostScrapEntity postScrap = QPostScrapEntity.postScrapEntity;
    private static final QPostCommentEntity postComment = QPostCommentEntity.postCommentEntity;
    private static final QUserEntity user = QUserEntity.userEntity;
    private static final QUserStatsEntity userStats = QUserStatsEntity.userStatsEntity;

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

    @Override
    public Page<PostDTOProjection> findPostsWithAllData(Pageable pageable, Long currentUserId) {
        String jpql = buildBaseQuery() + " WHERE p.status = 'ACTIVE' ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        if (currentUserId != null) {
            query.setParameter("currentUserId", currentUserId);
        }
        
        // 페이징 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<PostDTOProjection> results = query.getResultList();
        
        // 총 개수 조회
        long totalCount = getTotalCount("WHERE p.status = 'ACTIVE'");
        
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public Page<PostDTOProjection> findPostsByCategoryWithAllData(String category, Pageable pageable, Long currentUserId) {
        String jpql = buildBaseQuery() + " WHERE p.status = 'ACTIVE' AND p.postCategory = :category ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("category", category);
        if (currentUserId != null) {
            query.setParameter("currentUserId", currentUserId);
        }
        
        // 페이징 적용
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        List<PostDTOProjection> results = query.getResultList();
        
        // 총 개수 조회
        String countCondition = "WHERE p.status = 'ACTIVE' AND p.postCategory = '" + category + "'";
        long totalCount = getTotalCount(countCondition);
        
        return new PageImpl<>(results, pageable, totalCount);
    }


    private String buildBaseQuery() {
        return "SELECT NEW com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection(" +
                "p.postId, " +
                "p.postTitle, " +
                "p.postBody, " +
                "p.postCategory, " +
                "CAST(p.status AS string), " +
                "p.createdAt, " +
                "p.updatedAt, " +
                "p.postVisitCount, " +
                "p.userId, " +
                "u.nickname.value, " +
                "COALESCE(us.ratedRestCnt, 0), " +
                "COALESCE(COUNT(DISTINCT pl.postLikesId), 0L), " +
                "COALESCE(COUNT(DISTINCT pd.postDislikesId), 0L), " +
                "COALESCE(COUNT(DISTINCT CASE WHEN pc.status = 'ACTIVE' THEN pc.postCommentId END), 0L), " +
                "COALESCE(COUNT(DISTINCT ps.postScrapId), 0L), " +
                "COALESCE(MIN(pp.photoImgUrl), ''), " +
                "CASE WHEN :currentUserId IS NULL THEN false ELSE COALESCE(MAX(CASE WHEN upl.userId = :currentUserId THEN true ELSE false END), false) END, " +
                "CASE WHEN :currentUserId IS NULL THEN false ELSE COALESCE(MAX(CASE WHEN ups.userId = :currentUserId THEN true ELSE false END), false) END" +
                ") " +
                "FROM PostEntity p " +
                "LEFT JOIN UserEntity u ON p.userId = u.id " +
                "LEFT JOIN u.stats us " +
                "LEFT JOIN PostLikeEntity pl ON p.postId = pl.postId " +
                "LEFT JOIN PostDislikeEntity pd ON p.postId = pd.postId " +
                "LEFT JOIN PostCommentEntity pc ON p.postId = pc.postId " +
                "LEFT JOIN PostScrapEntity ps ON p.postId = ps.postId " +
                "LEFT JOIN PostPhotoEntity pp ON p.postId = pp.postId AND pp.status = 'ACTIVE' " +
                "LEFT JOIN PostLikeEntity upl ON p.postId = upl.postId " +
                "LEFT JOIN PostScrapEntity ups ON p.postId = ups.postId " +
                "GROUP BY p.postId, p.postTitle, p.postBody, p.postCategory, p.status, p.createdAt, p.updatedAt, p.postVisitCount, p.userId, u.nickname.value, us.ratedRestCnt ";
    }

    private long getTotalCount(String whereClause) {
        String countJpql = "SELECT COUNT(DISTINCT p.postId) FROM PostEntity p " + whereClause;
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        return countQuery.getSingleResult();
    }

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

}