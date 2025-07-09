package com.kustaurant.kustaurant.post.post.service.web;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.post.domain.*;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.domain.response.InteractionStatusResponse;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.*;
import com.kustaurant.kustaurant.post.post.service.port.*;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostScrapRepository postScrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final PostPhotoRepository postPhotoRepository;
    private final ImageExtractor imageExtractor;
    private final PostQueryDAO postQueryDAO;


    // 인기순 제한 기준 숫자
    public static final int POPULARCOUNT = 3;
    // 페이지 숫자
    public static final int PAGESIZE = 10;
    private final UserService userService;

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
    @Transactional
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

    // 조회수 증가
    @Transactional
    public void increaseVisitCount(Integer postId) {
        postRepository.increaseVisitCount(postId);
    }

    @Transactional
    public ReactionToggleResponse toggleLike(Integer postId, Long userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.LIKE_DELETED;
        } else if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_TO_LIKE;
        } else {
            postLikeRepository.save(new PostLike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_CREATED;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }



    @Transactional
    public ReactionToggleResponse toggleDislike(Integer postId, Long userId) {
        boolean isLikedBefore = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        boolean isDislikedBefore = postDislikeRepository.existsByUserIdAndPostId(userId, postId);

        ReactionStatus status;
        if (isDislikedBefore) {
            postDislikeRepository.deleteByUserIdAndPostId(userId, postId);
            status = ReactionStatus.DISLIKE_DELETED;
        } else if (isLikedBefore) {
            postLikeRepository.deleteByUserIdAndPostId(userId, postId);
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.LIKE_TO_DISLIKE;
        } else {
            postDislikeRepository.save(new PostDislike(userId, postId, LocalDateTime.now()));
            status = ReactionStatus.DISLIKE_CREATED;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = postLikeRepository.countByPostId(postId);
        int dislikeCount = postDislikeRepository.countByPostId(postId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
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


    @Transactional
    public void deletePost(Integer postId) {
        Post post = postRepository.findByIdWithComments(postId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));

        // 게시물 상태 변경
        post.delete();
        
        // 댓글 삭제 (ID 기반으로 처리)
        // 댓글들은 별도 서비스에서 처리하므로 여기서는 게시글만 삭제

        // 스크랩 삭제
        postScrapRepository.deleteByPostId(postId);

        // 사진 삭제
        postPhotoRepository.deleteByPostId(postId);

        // 저장
        postRepository.save(post);
    }

    @Transactional
    public Post create(String title, String category, String body, Long userId) {
        Post post = Post.builder().title(title).category(category).body(body).status(ContentStatus.ACTIVE).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).authorId(userId).build();
        Post savedPost = postRepository.save(post);

        List<String> imageUrls = imageExtractor.extract(body);

        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(savedPost.getId())
                    .photoImgUrl(imageUrl)
                    .status(ContentStatus.ACTIVE)
                    .build());
        }
        return savedPost;
    }


    @Transactional
    public void update(Integer postId, String title, String category, String body) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        List<String> imageUrls = imageExtractor.extract(body);
        post.update(title, body, category, imageUrls);

        postPhotoRepository.deleteByPostId(postId);
        // ID 기반으로 사진 저장
        for (String imageUrl : imageUrls) {
            postPhotoRepository.save(PostPhoto.builder()
                    .postId(postId)
                    .photoImgUrl(imageUrl)
                    .status(ContentStatus.ACTIVE)
                    .build());
        };
        postRepository.save(post);
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
