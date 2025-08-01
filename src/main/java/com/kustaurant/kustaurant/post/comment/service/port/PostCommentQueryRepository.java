package com.kustaurant.kustaurant.post.comment.service.port;

import com.kustaurant.kustaurant.post.comment.infrastructure.repo.projection.PostCommentDetailProjection;

import java.util.List;


public interface PostCommentQueryRepository {

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