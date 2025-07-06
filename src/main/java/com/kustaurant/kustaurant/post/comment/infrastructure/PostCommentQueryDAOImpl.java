package com.kustaurant.kustaurant.post.comment.infrastructure;

import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostCommentQueryDAOImpl implements PostCommentQueryDAO {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public List<PostCommentDTOProjection> findMyCommentedPostsWithDetails(Long currentUserId) {
        String jpql = buildMyCommentedPostsQuery();
        
        TypedQuery<PostCommentDTOProjection> query = entityManager.createQuery(jpql, PostCommentDTOProjection.class);
        query.setParameter("currentUserId", currentUserId);
        
        return query.getResultList();
    }

    /**
     * 마이페이지용 내가 작성한 댓글과 관련 게시글 정보를 조회하는 쿼리
     * 댓글 + 게시글 + 사용자 정보 + 좋아요/싫어요 수를 단일 쿼리로 조회
     */
    private String buildMyCommentedPostsQuery() {
        return "SELECT NEW com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection(" +
                "pc.commentId, " +
                "pc.commentBody, " +
                "CAST(pc.status AS string), " +
                "pc.parentCommentId, " +
                "pc.createdAt, " +
                "pc.updatedAt, " +
                "pc.postId, " +
                "pc.userId, " +
                "u.nickname.value, " +
                "COALESCE(u.rankImg, ''), " +
                "COALESCE(us.evaluationCount, 0), " +
                "COALESCE(COUNT(DISTINCT pcl.commentLikeId), 0L), " +
                "COALESCE(COUNT(DISTINCT pcd.commentDislikeId), 0L), " +
                "p.postTitle, " +
                "p.postCategory" +
                ") " +
                "FROM PostCommentEntity pc " +
                "LEFT JOIN UserEntity u ON pc.userId = u.id " +
                "LEFT JOIN u.stats us " +
                "LEFT JOIN PostEntity p ON pc.postId = p.postId " +
                "LEFT JOIN PostCommentLikeEntity pcl ON pc.commentId = pcl.commentId " +
                "LEFT JOIN PostCommentDislikeEntity pcd ON pc.commentId = pcd.commentId " +
                "WHERE pc.userId = :currentUserId AND pc.status = 'ACTIVE' AND p.status = 'ACTIVE' " +
                "GROUP BY pc.commentId, pc.commentBody, pc.status, pc.parentCommentId, pc.createdAt, pc.updatedAt, pc.postId, pc.userId, u.nickname.value, u.rankImg, us.evaluationCount, p.postTitle, p.postCategory " +
                "ORDER BY pc.createdAt DESC";
    }
}