package com.kustaurant.kustaurant.comment.controller.api;


import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.api.post.service.PostApiService;
import com.kustaurant.kustaurant.comment.controller.web.PostCommentService;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.comment.infrastructure.PostCommentApiRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.domain.ReactionToggleResponse;
import com.kustaurant.kustaurant.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.OUserService;
import com.kustaurant.kustaurant.post.domain.PostDTO;
import com.kustaurant.kustaurant.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.post.enums.ContentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostApiCommentService {
    private final PostCommentApiRepository postCommentApiRepository;
    private final PostRepository postRepository;
    private final PostApiService postApiService;
    private final PostCommentService postCommentService;

    // 댓글 생성
    public void create(PostEntity postEntity, Long userId, PostCommentEntity postComment) {
        postEntity.getPostCommentList().add(postComment);
        postRepository.save(postEntity);
    }

    // 댓글 조회
    public PostCommentEntity getPostCommentByCommentId(Integer commentId) {
        Optional<PostCommentEntity> postComment = postCommentApiRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new DataNotFoundException(COMMENT_NOT_FOUNT, commentId, "댓글");
        }
    }


    // 해당 댓글을 해당 유저가 좋아요를 눌렀는지 여부
    public boolean isLiked(PostCommentEntity postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postComment.getPostCommentLikesEntities().stream()
                .anyMatch(likeUser -> userId.equals(likeUser.getUserId()));
    }

    // 해당 댓글을 해당 유저가 싫어요를 눌렀는지 여부
    public boolean isDisliked(PostCommentEntity postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postComment.getPostCommentDislikesEntities().stream()
                .anyMatch(dislike -> userId.equals(dislike.getUserId()));
    }

    // 해당 댓글의 작성자인지 여부
    public boolean isCommentMine(PostCommentEntity postComment, Long userId) {
        if (userId == null || postComment == null) {
            return false;
        }
        return postComment.getUserId().equals(userId);
    }

    // 해당 글을 유저가 좋아요를 눌렀는지의 여부
    public boolean isLiked(PostEntity postEntity, Long userId) {
        if (userId == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostLikesList().stream()
                .anyMatch(postLikes -> userId.equals(postLikes.getUserId()));
    }

    // 해당 글을 해당 유저가 싫어요를 눌렀는지의 여부
    public boolean isScraped(PostEntity postEntity, Long userId) {
        if (userId == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostDislikesList().stream()
                .anyMatch(dislikeUser -> userId.equals(dislikeUser.getUserId()));
    }

    // 해당 글의 작성자인지 여부
    public boolean isPostMine(PostEntity postEntity, Long userId) {
        if (userId == null || postEntity == null) {
            return false;
        }
        return postEntity.getUserId().equals(userId);
    }

    // flags들 추가하여  PostComment DTO 생성
    public PostCommentDTO createPostCommentDTOWithFlags(PostCommentEntity postComment, Long userId) {
        PostCommentDTO postCommentDTO = PostCommentDTO.convertPostCommentToPostCommentDTO(postComment);
        if (userId != null) {
            // 각 댓글에 대해 좋아요, 싫어요, 나의 댓글인지 여부 계산
            boolean isLiked = isLiked(postComment, userId);
            boolean isDisliked = isDisliked(postComment, userId);
            boolean isCommentMine = isCommentMine(postComment, userId);
            postCommentDTO.setIsLiked(isLiked);
            postCommentDTO.setIsDisliked(isDisliked);
            postCommentDTO.setIsCommentMine(isCommentMine);

            // 대댓글 리스트에 대해 재귀적으로 flag 계산
            List<PostCommentDTO> replyDTOs = postComment.getRepliesList().stream()
                    .filter(reply -> reply.getStatus().equals("ACTIVE"))  // 활성화된 대댓글만 필터링
                    .sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed()) // 최신순 정렬
                    .map(reply -> createPostCommentDTOWithFlags(reply, userId))  // 재귀적으로 flag 계산
                    .collect(Collectors.toList());

            postCommentDTO.setRepliesList(replyDTOs);

        }

        return postCommentDTO;
    }


    public PostDTO createPostDTOWithFlags(PostEntity postEntity, Long userId) {
        // PostDTO 생성 및 post의 flag 설정
        PostDTO postDTO = PostDTO.convertPostToPostDTO(postEntity);
        if (userId != null) {
            postDTO.setIsPostMine(isPostMine(postEntity, userId));
            postDTO.setIsliked(isLiked(postEntity, userId));
            postDTO.setIsScraped(isScraped(postEntity, userId));
        }
        List<PostCommentDTO> commentDTOList = getPostCommentDTOs(postEntity, userId);

        postDTO.setPostCommentList(commentDTOList);


        return postDTO;
    }

    public List<PostCommentDTO> getPostCommentDTOs(PostEntity postEntity, Long userId) {
        // 각 댓글에 대한 flag 설정
        List<PostCommentDTO> commentDTOList = postEntity.getPostCommentList().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                .filter(comment -> comment.getStatus().equals("ACTIVE")) // 활성화된 댓글만 필터링
                .sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed()) // 최신순 정렬
                .map(comment -> createPostCommentDTOWithFlags(comment, userId))
                .collect(Collectors.toList());
        return commentDTOList;
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId, Long userId) {
        PostCommentEntity postComment = getPostCommentByCommentId(commentId);
        postComment.setStatus(ContentStatus.DELETED);

        // 대댓글 상태 변경
        postComment.getRepliesList().forEach(reply -> reply.setStatus(ContentStatus.DELETED));
        postCommentApiRepository.save(postComment);
    }

    // 댓글 생성
    public PostCommentEntity createComment(String content, String postId, Long userId) {
        PostEntity postEntity = postApiService.getPost(Integer.valueOf(postId));
        PostCommentEntity postComment = new PostCommentEntity(
                content,
                ContentStatus.ACTIVE,
                LocalDateTime.now(),
                postEntity,
                userId
        );
        return postCommentApiRepository.save(postComment);
    }

    // 대댓글 생성
    public void processParentComment(PostCommentEntity postComment, String parentCommentId) {
        PostCommentEntity parentComment = getPostCommentByCommentId(Integer.valueOf(parentCommentId));
        postComment.setParentComment(parentComment);
        parentComment.getRepliesList().add(postComment);
        postCommentApiRepository.save(parentComment);
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
}
