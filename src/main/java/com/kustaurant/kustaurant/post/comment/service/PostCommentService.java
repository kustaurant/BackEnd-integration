package com.kustaurant.kustaurant.post.comment.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentDislike;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentLike;
import com.kustaurant.kustaurant.post.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.post.comment.infrastructure.*;
import com.kustaurant.kustaurant.post.comment.infrastructure.projection.PostCommentDetailProjection;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentQueryDAO;
import com.kustaurant.kustaurant.post.post.domain.PostDetailView;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
import com.kustaurant.kustaurant.post.post.domain.response.InteractionStatusResponse;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.post.post.enums.LikeStatus;
import com.kustaurant.kustaurant.post.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.post.post.enums.ScrapStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import com.kustaurant.kustaurant.post.post.service.port.PostQueryDAO;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.service.web.PostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentQueryDAO postCommentQueryDAO;
    private final PostQueryDAO postQueryDAO;
    private final PostQueryService postQueryService;
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;
    private final UserService userService;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostCommentDislikeRepository postCommentDislikeRepository;

    @Transactional
    public void createComment(String content, Integer postId, Integer parentCommentId, Long userId) {
        PostComment comment = PostComment.create(content, userId, postId);
        if (parentCommentId != null) {
            PostComment parent = postCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "부모 댓글을 찾을 수 없습니다."));
            comment.setParentCommentId(parentCommentId);
        }
        postCommentRepository.save(comment);
    }

    @Transactional
    // 댓글 조회
    public PostComment getPostCommentByCommentId(Integer commentId) {
        Optional<PostComment> postComment = postCommentRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new DataNotFoundException(COMMENT_NOT_FOUND, commentId, "댓글");
        }
    }

    @Transactional
    public ReactionToggleResponse toggleLike(Long userId, Integer commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, commentId, "댓글"));

        boolean isLikedBefore = postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId);
        boolean isDislikedBefore = postCommentDislikeRepository.existsByUserIdAndCommentId(userId, commentId);
        log.info("toggleLike - userId: {}, evalId: {}, isLikedBefore: {}, isDislikedBefore: {}", userId, commentId, isLikedBefore, isDislikedBefore);
        ReactionStatus status;
        if (isLikedBefore) {
            postCommentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            status = ReactionStatus.LIKE_DELETED;
        } else if (isDislikedBefore) {
            postCommentDislikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            postCommentLikeRepository.save(PostCommentLike.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .createdAt(LocalDateTime.now())
                    .build());
            status = ReactionStatus.DISLIKE_TO_LIKE;
        } else {
            postCommentLikeRepository.save(PostCommentLike.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .createdAt(LocalDateTime.now())
                    .build());
            status = ReactionStatus.LIKE_CREATED;
        }

        int likeCount = postCommentLikeRepository.countByCommentId(commentId);
        int dislikeCount = postCommentDislikeRepository.countByCommentId(commentId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }


    @Transactional
    public ReactionToggleResponse toggleDislike(Long userId, Integer commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "댓글이 존재하지 않습니다."));

        boolean isLikedBefore = postCommentLikeRepository.existsByUserIdAndCommentId(userId, commentId);
        boolean isDislikedBefore = postCommentDislikeRepository.existsByUserIdAndCommentId(userId, commentId);

        ReactionStatus status;
        if (isDislikedBefore) {
            postCommentDislikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            status = ReactionStatus.DISLIKE_DELETED;
        } else if (isLikedBefore) {
            postCommentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            postCommentDislikeRepository.save(PostCommentDislike.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .createdAt(LocalDateTime.now())
                    .build());
            status = ReactionStatus.LIKE_TO_DISLIKE;
        } else {
            postCommentDislikeRepository.save(PostCommentDislike.builder()
                    .userId(userId)
                    .commentId(commentId)
                    .createdAt(LocalDateTime.now())
                    .build());
            status = ReactionStatus.DISLIKE_CREATED;
        }

        int likeCount = postCommentLikeRepository.countByCommentId(commentId);
        int dislikeCount = postCommentDislikeRepository.countByCommentId(commentId);

        return new ReactionToggleResponse(status, likeCount - dislikeCount, likeCount, dislikeCount);
    }





    public InteractionStatusResponse getUserInteractionStatus(Integer commentId, Long userId) {
        if (userId == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }
        boolean isLiked = postCommentLikeJpaRepository.existsByUserIdAndCommentId(userId, commentId);
        boolean isDisliked = postCommentDislikeJpaRepository.existsByUserIdAndCommentId(userId, commentId);
        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
    }

    @Transactional(readOnly = true)
    public PostDetailView buildPostDetailView(Integer postId, Long userId, String sort) {
        // 1. 게시글 정보 조회 (최적화된 단일 쿼리)
        PostDTOProjection postData = postQueryDAO.findPostWithAllData(postId, userId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));
        
        // 2. 댓글 트리 조회 (최적화된 단일 쿼리)
        List<PostCommentDetailProjection> commentData = 
                postCommentQueryDAO.findCommentTreeByPostId(postId, userId, sort);
        
        // 3. 댓글 계층 구조 구성 및 DTO 변환
        List<PostCommentDTO> commentDTOs = buildCommentTree(commentData);
        
        // 4. PostDTO 생성 및 댓글 리스트 설정
        PostDTO postDTO = PostDTO.from(postData);
        postDTO.setPostCommentList(commentDTOs);
        postDTO.setIsPostMine(userId != null && userId.equals(postData.authorId()));
        
        // 5. 현재 사용자 정보 조회 (필요한 경우에만)
        UserDTO currentUserDTO = null;
        if (userId != null) {
            User currentUser = userService.getUserById(userId);
            currentUserDTO = UserDTO.from(currentUser);
        }
        
        // 6. 최종 뷰 조합
        return PostDetailView.builder()
                .post(postDTO)
                .currentUser(currentUserDTO)
                .sort(sort)
                .postInteractionStatus(postQueryService.getUserInteractionStatus(postId, userId))
                .commentInteractionMap(null) // 더 이상 사용하지 않음 - PostCommentDTO에 직접 포함됨
                .build();
    }

    /**
     * 댓글 데이터를 계층 구조로 구성
     * 부모 댓글과 대댓글을 트리 형태로 조직화
     */
    private List<PostCommentDTO> buildCommentTree(List<PostCommentDetailProjection> commentData) {
        // 1. 모든 댓글을 DTO로 변환
        List<PostCommentDTO> allComments = commentData.stream()
                .map(PostCommentDTO::from)
                .toList();
        
        // 2. 부모 댓글과 대댓글을 분리
        Map<Integer, PostCommentDTO> commentMap = new HashMap<>();
        List<PostCommentDTO> parentComments = new ArrayList<>();
        List<PostCommentDTO> replyComments = new ArrayList<>();
        
        for (PostCommentDTO comment : allComments) {
            commentMap.put(comment.getCommentId(), comment);
            if (comment.getCommentId() != null && commentData.stream()
                    .anyMatch(p -> p.commentId().equals(comment.getCommentId()) && p.parentCommentId() == null)) {
                parentComments.add(comment);
            } else {
                replyComments.add(comment);
            }
        }
        
        // 3. 대댓글을 부모 댓글에 연결
        for (PostCommentDTO reply : replyComments) {
            Integer parentId = commentData.stream()
                    .filter(p -> p.commentId().equals(reply.getCommentId()))
                    .map(PostCommentDetailProjection::parentCommentId)
                    .findFirst()
                    .orElse(null);
            
            if (parentId != null && commentMap.containsKey(parentId)) {
                PostCommentDTO parent = commentMap.get(parentId);
                if (parent.getRepliesList() == null) {
                    parent.setRepliesList(new ArrayList<>());
                } else if (!(parent.getRepliesList() instanceof ArrayList)) {
                    parent.setRepliesList(new ArrayList<>(parent.getRepliesList()));
                }
                parent.getRepliesList().add(reply);
            }
        }
        
        return parentComments;
    }

    @Transactional
    public int deleteComment(Integer commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, commentId, "댓글"));

        comment.delete();  // 도메인 내에서 댓글 상태 변경

        // ID 기반으로 대댓글 조회 및 삭제
        List<PostComment> replies = postCommentRepository.findByParentCommentId(commentId);
        for (PostComment reply : replies) {
            reply.delete();
        }
        
        postCommentRepository.save(comment); // 변경 반영
        if (!replies.isEmpty()) {
            postCommentRepository.saveAll(replies); // 대댓글 변경 반영
        }

        return 1 + replies.size(); // 삭제된 댓글 수 리턴
    }
}
