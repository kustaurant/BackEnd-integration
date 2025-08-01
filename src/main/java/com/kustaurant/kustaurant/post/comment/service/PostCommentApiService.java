package com.kustaurant.kustaurant.post.comment.service;


import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.post.domain.dto.PostDTO;
import com.kustaurant.kustaurant.post.post.service.api.PostQueryApiService;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentLikeRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentDislikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostLikeRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostDislikeRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.domain.response.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PostCommentApiService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostCommentDislikeRepository postCommentDislikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final PostRepository postRepository;
    private final PostQueryApiService postQueryApiService;
    private final PostCommentService postCommentService;
    private final UserService userService;

    // 댓글 생성
    public void create(Post post, Long userId, PostComment postComment) {
        // ID 기반으로 댓글 생성
        postComment.setPostId(post.getId());
        postCommentRepository.save(postComment);
    }

    // 댓글 조회
    public PostComment getPostCommentByCommentId(Integer commentId) {
        Optional<PostComment> postComment = postCommentRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new DataNotFoundException(COMMENT_NOT_FOUND, commentId, "댓글");
        }
    }


    // 해당 댓글을 해당 유저가 좋아요를 눌렀는지 여부
    public boolean isLiked(PostComment postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postCommentLikeRepository.existsByUserIdAndCommentId(userId, postComment.getId());
    }

    // 해당 댓글을 해당 유저가 싫어요를 눌렀는지 여부
    public boolean isDisliked(PostComment postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postCommentDislikeRepository.existsByUserIdAndCommentId(userId, postComment.getId());
    }

    // 해당 댓글의 작성자인지 여부
    public boolean isCommentMine(PostComment postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postComment.getUserId().equals(userId);
    }

    // 해당 글을 유저가 좋아요를 눌렀는지의 여부
    public boolean isLiked(Post post, Long userId) {
        if (userId == null || post == null) {
            return false;
        }
        return postLikeRepository.existsByUserIdAndPostId(userId, post.getId());
    }

    // 해당 글을 해당 유저가 싫어요를 눌렀는지의 여부
    public boolean isDisliked(Post post, Long userId) {
        if (userId == null || post == null) {
            return false;
        }
        return postDislikeRepository.existsByUserIdAndPostId(userId, post.getId());
    }

    // 해당 글의 작성자인지 여부
    public boolean isPostMine(Post post, Long userId) {
        if (userId == null || post == null) {
            return false;
        }
        return post.getAuthorId().equals(userId);
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId, Long userId) {
        PostComment postComment = getPostCommentByCommentId(commentId);
        postComment.setStatus(ContentStatus.DELETED);

        // 대댓글 상태 변경 - ID 기반으로 처리
        List<PostComment> replies = postCommentRepository.findByParentCommentId(commentId);
        for (PostComment reply : replies) {
            reply.setStatus(ContentStatus.DELETED);
        }
        postCommentRepository.saveAll(replies);
        postCommentRepository.save(postComment);
    }

    // 댓글 생성
    public PostComment createComment(String content, String postId, Long userId) {
        // 댓글 내용 검증
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        
        Post post = postQueryApiService.getPost(Integer.valueOf(postId));
        PostComment postComment = PostComment.create(content.trim(), userId, post.getId());
        return postCommentRepository.save(postComment);
    }

    // 대댓글 생성
    public void processParentComment(PostComment postComment, String parentCommentId) {
        PostComment parentComment = getPostCommentByCommentId(Integer.valueOf(parentCommentId));
        postComment.setParentCommentId(parentComment.getId());
        postCommentRepository.save(postComment);
    }

    public ReactionToggleResponse toggleCommentLikeOrDislike(String action, Long userId, Integer commentId) {
        ReactionToggleResponse reactionToggleResponse;
        if ("likes".equals(action)) {
            reactionToggleResponse = postCommentService.toggleLike(userId, commentId);
        } else if ("dislikes".equals(action)) {
            reactionToggleResponse = postCommentService.toggleDislike(userId, commentId);
        } else {
            throw new IllegalArgumentException("action 값이 유효하지 않습니다.");
        }
        return reactionToggleResponse;
    }

    // PostDTO와 플래그를 생성하는 메서드
    public PostDTO createPostDTOWithFlags(Post post, Long userId) {
        User user = userService.getUserById(userId);
        return PostDTO.from(post, user);
    }

    // 댓글 DTO 리스트를 가져오는 메서드
    public List<PostCommentDTO> getPostCommentDTOs(Post post, Long userId) {
        List<PostComment> comments = postCommentRepository.findByPostId(post.getId());
        User user = userService.getUserById(userId);

        return comments.stream().map(c -> PostCommentDTO.from(c, user)).toList();
    }

    // 댓글 DTO와 플래그를 생성하는 메서드
    public PostCommentDTO createPostCommentDTOWithFlags(PostComment postComment, Long userId) {
        // PostCommentDTO 생성 로직 구현
        // 실제 구현에서는 PostCommentDTO 생성 및 플래그 설정 로직이 필요합니다
        return PostCommentDTO.builder()
                .commentId(postComment.getId())
                .commentBody(postComment.getCommentBody())
                .status(postComment.getStatus().name())
                .likeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .dislikeCount(0) // 리액션 지표는 별도 서비스에서 계산
                .createdAt(postComment.getCreatedAt())
                .updatedAt(postComment.getUpdatedAt())
                .timeAgo(TimeAgoUtil.toKor(postComment.getCreatedAt()))
                .isLiked(isLiked(postComment, userId))
                .isDisliked(isDisliked(postComment, userId))
                .isCommentMine(isCommentMine(postComment, userId))
                .repliesList(new ArrayList<>())
                .build();
    }
}
