package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;
import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDetailProjection;

import java.util.List;

/**
 * 댓글 관련 복잡한 쿼리를 위한 DAO 인터페이스
 * PostQueryDAO와 유사한 패턴으로 댓글 데이터 최적화 조회
 */
public interface PostCommentQueryDAO {
    
    /**
     * 마이페이지용: 내가 작성한 댓글과 관련 게시글 정보를 함께 조회
     * 댓글 + 게시글 + 사용자 정보를 단일 쿼리로 조회하여 N+1 문제 해결
     * 
     * @param currentUserId 현재 사용자 ID
     * @return 댓글과 관련 정보가 포함된 projection 리스트
     */
    List<PostCommentDTOProjection> findMyCommentedPostsWithDetails(Long currentUserId);
    
    /**
     * 게시글 상세화면용: 특정 게시글의 댓글 트리를 모든 관련 데이터와 함께 조회
     * 댓글 + 사용자 정보 + 반응 정보 + 현재 사용자의 상호작용 상태를 단일 쿼리로 조회
     * 
     * @param postId 게시글 ID
     * @param currentUserId 현재 사용자 ID (상호작용 상태 확인용, null 가능)
     * @param sort 정렬 방식 ("recent" 또는 "popular")
     * @return 댓글 트리와 관련 정보가 포함된 projection 리스트
     */
    List<PostCommentDetailProjection> findCommentTreeByPostId(Integer postId, Long currentUserId, String sort);
}