package com.kustaurant.kustaurant.api.comment;


import com.kustaurant.kustaurant.api.post.service.PostApiService;
import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentEntity;
import com.kustaurant.kustaurant.common.comment.infrastructure.PostCommentApiRepository;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.UserService;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.post.domain.PostDTO;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import com.kustaurant.kustaurant.web.comment.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostApiCommentService {
    private final PostCommentApiRepository postCommentApiRepository;
    private final OUserRepository userRepository;
    private final PostRepository postRepository;
    private final PostApiService postApiService;
    private final UserService userService;
    private final PostCommentService postCommentService;

    // 댓글 생성
    public void create(PostEntity postEntity, UserEntity user, PostCommentEntity postComment) {
        user.getPostCommentList().add(postComment);
        postEntity.getPostCommentList().add(postComment);
        userRepository.save(user);
        postRepository.save(postEntity);
    }

    // 댓글 조회
    public PostCommentEntity getPostCommentByCommentId(Integer commentId) {
        Optional<PostCommentEntity> postComment = postCommentApiRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new OptionalNotExistException("해당 id의 댓글이 존재하지 않습니다.");
        }
    }


    public int toggleCommentLike(PostCommentEntity postComment, UserEntity user) {
        Map<String, Object> result = postCommentService.toggleCommentLike(postComment, user);
        ReactionStatus status = ReactionStatus.valueOf(result.keySet().iterator().next());
        return status.toAppLikeStatus();
    }

    public int toggleCommentDislike(PostCommentEntity postComment, UserEntity user) {
        Map<String, Object> result = postCommentService.toggleCommentDislike(postComment, user);
        ReactionStatus status = ReactionStatus.valueOf(result.keySet().iterator().next());
        return status.toAppLikeStatus();
    }


    // 해당 댓글을 해당 유저가 좋아요를 눌렀는지 여부
    public boolean isLiked(PostCommentEntity postComment, UserEntity user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getPostCommentLikesEntities().stream()
                .anyMatch(likeUser -> likeUser.equals(user));
    }

    // 해당 댓글을 해당 유저가 싫어요를 눌렀는지 여부
    public boolean isDisliked(PostCommentEntity postComment, UserEntity user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getPostCommentDislikesEntities().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(user));
    }

    // 해당 댓글의 작성자인지 여부
    public boolean isCommentMine(PostCommentEntity postComment, UserEntity user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getUser().equals(user);
    }

    // 해당 글을 유저가 좋아요를 눌렀는지의 여부
    public boolean isLiked(PostEntity postEntity, UserEntity user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostLikesList().stream()
                .anyMatch(postLikesEntity -> postLikesEntity.getUser().equals(user));
    }

    // 해당 글을 해당 유저가 싫어요를 눌렀는지의 여부
    public boolean isScraped(PostEntity postEntity, UserEntity user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostDislikesList().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(user));
    }

    // 해당 글의 작성자인지 여부
    public boolean isPostMine(PostEntity postEntity, UserEntity user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getUser().equals(user);
    }

    // flags들 추가하여  PostComment DTO 생성
    public PostCommentDTO createPostCommentDTOWithFlags(PostCommentEntity postComment, UserEntity user) {
        PostCommentDTO postCommentDTO = PostCommentDTO.convertPostCommentToPostCommentDTO(postComment);
        if (user != null) {
            // 각 댓글에 대해 좋아요, 싫어요, 나의 댓글인지 여부 계산
            boolean isLiked = isLiked(postComment, user);
            boolean isDisliked = isDisliked(postComment, user);
            boolean isCommentMine = isCommentMine(postComment, user);
            postCommentDTO.setIsLiked(isLiked);
            postCommentDTO.setIsDisliked(isDisliked);
            postCommentDTO.setIsCommentMine(isCommentMine);

            // 대댓글 리스트에 대해 재귀적으로 flag 계산
            List<PostCommentDTO> replyDTOs = postComment.getRepliesList().stream()
                    .filter(reply -> reply.getStatus().equals("ACTIVE"))  // 활성화된 대댓글만 필터링
                    .sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed()) // 최신순 정렬
                    .map(reply -> createPostCommentDTOWithFlags(reply, user))  // 재귀적으로 flag 계산
                    .collect(Collectors.toList());

            postCommentDTO.setRepliesList(replyDTOs);

        }

        return postCommentDTO;
    }


    public PostDTO createPostDTOWithFlags(PostEntity postEntity, UserEntity user) {
        // PostDTO 생성 및 post의 flag 설정
        PostDTO postDTO = PostDTO.convertPostToPostDTO(postEntity);
        if (user != null) {
            postDTO.setIsPostMine(isPostMine(postEntity, user));
            postDTO.setIsliked(isLiked(postEntity, user));
            postDTO.setIsScraped(isScraped(postEntity, user));
        }
        List<PostCommentDTO> commentDTOList = getPostCommentDTOs(postEntity, user);

        postDTO.setPostCommentList(commentDTOList);


        return postDTO;
    }

    public List<PostCommentDTO> getPostCommentDTOs(PostEntity postEntity, UserEntity user) {
        // 각 댓글에 대한 flag 설정
        List<PostCommentDTO> commentDTOList = postEntity.getPostCommentList().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                .filter(comment -> comment.getStatus().equals("ACTIVE")) // 활성화된 댓글만 필터링
                .sorted(Comparator.comparing(PostCommentEntity::getCreatedAt).reversed()) // 최신순 정렬
                .map(comment -> createPostCommentDTOWithFlags(comment, user))
                .collect(Collectors.toList());
        return commentDTOList;
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId, Integer userId) {
        PostCommentEntity postComment = getPostCommentByCommentId(commentId);
        postComment.setStatus("DELETED");

        // 대댓글 상태 변경
        postComment.getRepliesList().forEach(reply -> reply.setStatus("DELETED"));
        postCommentApiRepository.save(postComment);
    }

    // 댓글 생성
    public PostCommentEntity createComment(String content, String postId, Integer userId) {
        UserEntity user = userService.findUserById(userId);
        PostEntity postEntity = postApiService.getPost(Integer.valueOf(postId));
        PostCommentEntity postComment = new PostCommentEntity(content, PostStatus.ACTIVE.name(), LocalDateTime.now(), postEntity, user);
        return postCommentApiRepository.save(postComment);
    }

    // 대댓글 생성
    public void processParentComment(PostCommentEntity postComment, String parentCommentId) {
        PostCommentEntity parentComment = getPostCommentByCommentId(Integer.valueOf(parentCommentId));
        postComment.setParentComment(parentComment);
        parentComment.getRepliesList().add(postComment);
        postCommentApiRepository.save(parentComment);
    }

    public int toggleCommentLikeOrDislike(String action, PostCommentEntity postComment, UserEntity user) {
        int commentLikeStatus;
        if ("likes".equals(action)) {
            commentLikeStatus = toggleCommentLike(postComment, user);
        } else if ("dislikes".equals(action)) {
            commentLikeStatus = toggleCommentDislike(postComment, user);
        } else {
            throw new IllegalArgumentException("action 값이 유효하지 않습니다.");
        }
        return commentLikeStatus;
    }
}
