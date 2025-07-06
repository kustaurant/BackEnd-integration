package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDTOProjection;

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
}