package com.kustaurant.kustaurant.post.post.infrastructure;

import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Optional<PostDTOProjection> findPostWithAllData(Integer postId, Long currentUserId) {
        String jpql = buildBaseQuery() + " WHERE p.postId = :postId AND p.status = 'ACTIVE'";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("postId", postId);
        if (currentUserId != null) {
            query.setParameter("currentUserId", currentUserId);
        }
        
        List<PostDTOProjection> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
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

    @Override
    public List<PostDTOProjection> findPostsByAuthorWithAllData(Long authorId, Long currentUserId) {
        String jpql = buildBaseQuery() + " WHERE p.status = 'ACTIVE' AND p.userId = :authorId ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("authorId", authorId);
        if (currentUserId != null) {
            query.setParameter("currentUserId", currentUserId);
        }
        
        return query.getResultList();
    }

    @Override
    public List<PostDTOProjection> findScrappedPostsByUserWithAllData(Long userId, Long currentUserId) {
        String jpql = buildBaseQuery() + 
                " WHERE p.status = 'ACTIVE' AND EXISTS (SELECT 1 FROM PostScrapEntity ps WHERE ps.postId = p.postId AND ps.userId = :userId) " +
                " ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("userId", userId);
        if (currentUserId != null) {
            query.setParameter("currentUserId", currentUserId);
        }
        
        return query.getResultList();
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
                "COALESCE(u.rankImg, ''), " +
                "COALESCE(us.evaluationCount, 0), " +
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
                "GROUP BY p.postId, p.postTitle, p.postBody, p.postCategory, p.status, p.createdAt, p.updatedAt, p.postVisitCount, p.userId, u.nickname.value, u.rankImg, us.evaluationCount ";
    }

    private long getTotalCount(String whereClause) {
        String countJpql = "SELECT COUNT(DISTINCT p.postId) FROM PostEntity p " + whereClause;
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        return countQuery.getSingleResult();
    }

    @Override
    public List<PostDTOProjection> findMyWrittenPosts(Long currentUserId) {
        String jpql = buildMyPageQuery() + " WHERE p.status = 'ACTIVE' AND p.userId = :currentUserId ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("currentUserId", currentUserId);
        
        return query.getResultList();
    }

    @Override
    public List<PostDTOProjection> findMyCommentedPosts(Long currentUserId) {
        String jpql = buildMyPageQuery() + 
                " WHERE p.status = 'ACTIVE' AND EXISTS (" +
                "   SELECT 1 FROM PostCommentEntity pc " +
                "   WHERE pc.postId = p.postId AND pc.userId = :currentUserId AND pc.status = 'ACTIVE'" +
                " ) ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("currentUserId", currentUserId);
        
        return query.getResultList();
    }

    @Override
    public List<PostDTOProjection> findMyScrappedPosts(Long currentUserId) {
        String jpql = buildMyPageQuery() + 
                " WHERE p.status = 'ACTIVE' AND EXISTS (" +
                "   SELECT 1 FROM PostScrapEntity ps " +
                "   WHERE ps.postId = p.postId AND ps.userId = :currentUserId" +
                " ) ORDER BY p.createdAt DESC";
        
        TypedQuery<PostDTOProjection> query = entityManager.createQuery(jpql, PostDTOProjection.class);
        query.setParameter("currentUserId", currentUserId);
        
        return query.getResultList();
    }

    /**
     * 마이페이지용 간소화된 쿼리 (상호작용 정보 제외)
     */
    private String buildMyPageQuery() {
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
                "COALESCE(u.rankImg, ''), " +
                "COALESCE(us.evaluationCount, 0), " +
                "COALESCE(COUNT(DISTINCT pl.postLikesId), 0L), " +
                "COALESCE(COUNT(DISTINCT pd.postDislikesId), 0L), " +
                "COALESCE(COUNT(DISTINCT CASE WHEN pc.status = 'ACTIVE' THEN pc.postCommentId END), 0L), " +
                "COALESCE(COUNT(DISTINCT ps.postScrapId), 0L), " +
                "COALESCE(MIN(pp.photoImgUrl), ''), " +
                "false, " + // isLiked - 마이페이지에서는 불필요
                "false" +   // isScraped - 마이페이지에서는 불필요  
                ") " +
                "FROM PostEntity p " +
                "LEFT JOIN UserEntity u ON p.userId = u.id " +
                "LEFT JOIN u.stats us " +
                "LEFT JOIN PostLikeEntity pl ON p.postId = pl.postId " +
                "LEFT JOIN PostDislikeEntity pd ON p.postId = pd.postId " +
                "LEFT JOIN PostCommentEntity pc ON p.postId = pc.postId " +
                "LEFT JOIN PostScrapEntity ps ON p.postId = ps.postId " +
                "LEFT JOIN PostPhotoEntity pp ON p.postId = pp.postId AND pp.status = 'ACTIVE' " +
                "GROUP BY p.postId, p.postTitle, p.postBody, p.postCategory, p.status, p.createdAt, p.updatedAt, p.postVisitCount, p.userId, u.nickname.value, u.rankImg, us.evaluationCount ";
    }
}