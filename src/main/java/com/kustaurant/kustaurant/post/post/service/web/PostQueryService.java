package com.kustaurant.kustaurant.post.post.service.web;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.domain.response.InteractionStatusResponse;
import com.kustaurant.kustaurant.post.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.post.post.enums.LikeStatus;
import com.kustaurant.kustaurant.post.post.enums.ScrapStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.post.post.service.port.PostDislikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostLikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.POST_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService {
    private final PostRepository postRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final PostQueryDAO postQueryDAO;

    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;

    // 메인 화면 로딩하기
    public Page<PostDTO> getList(int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        
        // 최신순 정렬
        if (sort.isEmpty() || sort.equals("recent")) {
            return postQueryDAO.findPostsWithAllData(pageable, null)
                    .map(PostDTO::from);
        }
        // 인기순 정렬하기
        else {
            return postQueryDAO.findPopularPostsWithAllData(pageable, null, POPULARCOUNT)
                    .map(PostDTO::from);
        }
    }

    // 검색결과 반환
    public Page<PostDTO> getList(int page, String sort, String kw, String postCategory) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        
        // 인기순 정렬
        if (sort.equals("popular")) {
            return postQueryDAO.findPopularPostsBySearchKeywordWithAllData(kw, postCategory, pageable, null, POPULARCOUNT)
                    .map(PostDTO::from);
        } else {
            // 최신순 정렬
            return postQueryDAO.findPostsBySearchKeywordWithAllData(kw, postCategory, pageable, null)
                    .map(PostDTO::from);
        }
    }

    //  드롭다운에서 카테고리가 설정된 상태에서 게시물 반환하기
    public Page<PostDTO> getListByPostCategory(String postCategory, int page, String sort) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, PAGESIZE, Sort.by(sorts));
        
        if (sort.equals("popular")) {
            return postQueryDAO.findPopularPostsByCategoryWithAllData(postCategory, pageable, null, POPULARCOUNT)
                    .map(PostDTO::from);
        } else {
            return postQueryDAO.findPostsByCategoryWithAllData(postCategory, pageable, null)
                    .map(PostDTO::from);
        }
    }

    @Transactional(readOnly = true)
    public Post getPost(Integer id) {
        Optional<Post> post = this.postRepository.findById(id);
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataNotFoundException(POST_NOT_FOUND, id, "게시글");
        }
    }

    // 게시물 리스트에 대한 시간 경과 리스트로 반환하는 함수.
    public List<String> getTimeAgoList(Page<PostDTO> postList) {
        return postList.stream().map(post -> {
            LocalDateTime createdAt = post.getCreatedAt();
            return TimeAgoUtil.toKor(createdAt);
        }).collect(Collectors.toList());
    }

    public InteractionStatusResponse getUserInteractionStatus(Integer postId, Long userId) {
        if (userId == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }

        boolean isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDisliked = postDislikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isScrapped = postScrapRepository.existsByUserIdAndPostId(userId, postId);

        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, isScrapped ? ScrapStatus.SCRAPPED : ScrapStatus.NOT_SCRAPPED);
    }

    /**
     * PostQueryDAO를 활용한 최적화된 게시글 상세 조회
     * 모든 관련 데이터를 단일 쿼리로 조회하여 N+1 문제 해결
     */
    public Optional<PostDTO> getPostWithAllData(Integer postId, Long currentUserId) {
        Optional<PostDTOProjection> projection = postQueryDAO.findPostWithAllData(postId, currentUserId);
        return projection.map(PostDTO::from);
    }
    
    /**
     * PostQueryDAO를 활용한 최적화된 게시글 목록 조회
     * 모든 관련 데이터를 단일 쿼리로 조회하여 N+1 문제 해결
     */
    public Page<PostDTO> getPostsWithAllData(Pageable pageable, Long currentUserId) {
        Page<PostDTOProjection> projections = postQueryDAO.findPostsWithAllData(pageable, currentUserId);
        return projections.map(PostDTO::from);
    }
    
    /**
     * 카테고리별 게시글 목록 조회 (최적화된 버전)
     */
    public Page<PostDTO> getPostsByCategoryWithAllData(String category, Pageable pageable, Long currentUserId) {
        Page<PostDTOProjection> projections = postQueryDAO.findPostsByCategoryWithAllData(category, pageable, currentUserId);
        return projections.map(PostDTO::from);
    }
}