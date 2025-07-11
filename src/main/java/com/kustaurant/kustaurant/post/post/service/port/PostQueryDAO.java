package com.kustaurant.kustaurant.post.post.service.port;

import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostQueryDAO {
    
    /**
     * 게시글 상세 정보를 모든 관련 데이터와 함께 조회
     * @param postId 게시글 ID
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @return PostDTOProjection 또는 Optional.empty()
     */
    Optional<PostDTOProjection> findPostWithAllData(Integer postId, Long currentUserId);
    
    /**
     * 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPostsWithAllData(Pageable pageable, Long currentUserId);
    
    /**
     * 특정 카테고리 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPostsByCategoryWithAllData(String category, Pageable pageable, Long currentUserId);
    
    /**
     * 마이페이지용: 내가 작성한 게시글 목록 조회 (상호작용 정보 제외)
     * @param currentUserId 현재 사용자 ID
     * @return List<PostDTOProjection>
     */
    List<PostDTOProjection> findMyWrittenPosts(Long currentUserId);
    
    /**
     * 마이페이지용: 내가 스크랩한 게시글 목록 조회 (상호작용 정보 제외)
     * @param currentUserId 현재 사용자 ID
     * @return List<PostDTOProjection>
     */
    List<PostDTOProjection> findMyScrappedPosts(Long currentUserId);
    
    /**
     * 인기 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @param minLikeCount 최소 좋아요 수
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPopularPostsWithAllData(Pageable pageable, Long currentUserId, int minLikeCount);
    
    /**
     * 특정 카테고리의 인기 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param category 카테고리
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @param minLikeCount 최소 좋아요 수
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPopularPostsByCategoryWithAllData(String category, Pageable pageable, Long currentUserId, int minLikeCount);
    
    /**
     * 검색 키워드로 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param keyword 검색 키워드 (제목, 내용 검색)
     * @param category 카테고리 (null 가능)
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPostsBySearchKeywordWithAllData(String keyword, String category, Pageable pageable, Long currentUserId);
    
    /**
     * 검색 키워드로 인기 게시글 목록을 모든 관련 데이터와 함께 페이징 조회
     * @param keyword 검색 키워드 (제목, 내용 검색)
     * @param category 카테고리 (null 가능)
     * @param pageable 페이징 정보
     * @param currentUserId 현재 사용자 ID (좋아요/스크랩 여부 확인용, null 가능)
     * @param minLikeCount 최소 좋아요 수
     * @return Page<PostDTOProjection>
     */
    Page<PostDTOProjection> findPopularPostsBySearchKeywordWithAllData(String keyword, String category, Pageable pageable, Long currentUserId, int minLikeCount);
}