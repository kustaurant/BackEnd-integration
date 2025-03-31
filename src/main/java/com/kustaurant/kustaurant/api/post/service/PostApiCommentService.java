package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.UserService;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.post.domain.PostDTO;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.domain.PostCommentDTO;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostApiCommentService {
    private final PostCommentApiRepository postCommentApiRepository;
    private final OUserRepository OUserRepository;
    private final PostRepository postRepository;
    private final PostApiService postApiService;
    private final UserService userService;

    // 댓글 생성
    public void create(PostEntity postEntity, UserEntity UserEntity, PostComment postComment) {
        UserEntity.getPostCommentList().add(postComment);
        postEntity.getPostCommentList().add(postComment);
        OUserRepository.save(UserEntity);
        postRepository.save(postEntity);
    }

    // 댓글 조회
    public PostComment getPostCommentByCommentId(Integer commentId) {
        Optional<PostComment> postComment = postCommentApiRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new OptionalNotExistException("해당 id의 댓글이 존재하지 않습니다.");
        }
    }


    // 댓글 좋아요 (세 가지 경우)
    public int likeCreateOrDelete(PostComment postComment, UserEntity UserEntity) {
        List<UserEntity> likeUserEntityList = postComment.getLikeUserList();
        List<UserEntity> dislikeUserEntityList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = UserEntity.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = UserEntity.getDislikePostCommentList();
        int commentLikeStatus;

        // 해당 postComment를 like 한 경우 - 제거
        if (likeUserEntityList.contains(UserEntity)) {
            postComment.setLikeCount(postComment.getLikeCount() - 1);
            likePostCommentList.remove(postComment);
            likeUserEntityList.remove(UserEntity);
            commentLikeStatus = 0; // 아무것도 누르지 않은 상태로 변경
        }
        // 해당 postComment를 이미 dislike 한 경우 - 제거하고 like 추가
        else if (dislikeUserEntityList.contains(UserEntity)) {
            postComment.setLikeCount(postComment.getLikeCount() + 2);
            dislikeUserEntityList.remove(UserEntity);
            dislikePostCommentList.remove(postComment);
            likeUserEntityList.add(UserEntity);
            likePostCommentList.add(postComment);
            commentLikeStatus = 1; // 좋아요 상태로 변경
        }
        // 처음 like 하는 경우 - 추가
        else {
            postComment.setLikeCount(postComment.getLikeCount() + 1);
            likeUserEntityList.add(UserEntity);
            likePostCommentList.add(postComment);
            commentLikeStatus = 1; // 좋아요 상태로 변경
        }

        postCommentApiRepository.save(postComment);
        OUserRepository.save(UserEntity);

        return commentLikeStatus;
    }

    // 댓글 싫어요 (세 가지 경우)
    public int dislikeCreateOrDelete(PostComment postComment, UserEntity UserEntity) {
        List<UserEntity> likeUserEntityList = postComment.getLikeUserList();
        List<UserEntity> dislikeUserEntityList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = UserEntity.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = UserEntity.getDislikePostCommentList();
        int commentLikeStatus;

        // 해당 postComment를 dislike 한 경우 - 제거
        if (dislikeUserEntityList.contains(UserEntity)) {
            postComment.setLikeCount(postComment.getLikeCount() + 1); // 싫어요를 제거하므로 좋아요 수 증가
            dislikePostCommentList.remove(postComment);
            dislikeUserEntityList.remove(UserEntity);
            commentLikeStatus = 0; // 아무것도 누르지 않은 상태로 변경
        }
        // 해당 postComment를 이미 like 한 경우 - 제거하고 dislike 추가
        else if (likeUserEntityList.contains(UserEntity)) {
            postComment.setLikeCount(postComment.getLikeCount() - 2);
            likeUserEntityList.remove(UserEntity);
            likePostCommentList.remove(postComment);
            dislikeUserEntityList.add(UserEntity);
            dislikePostCommentList.add(postComment);
            commentLikeStatus = -1; // 싫어요 상태로 변경
        }
        // 처음 dislike 하는 경우 - 추가
        else {
            postComment.setLikeCount(postComment.getLikeCount() - 1);
            dislikeUserEntityList.add(UserEntity);
            dislikePostCommentList.add(postComment);
            commentLikeStatus = -1; // 싫어요 상태로 변경
        }

        postCommentApiRepository.save(postComment);
        OUserRepository.save(UserEntity);

        return commentLikeStatus;
    }


    // 해당 댓글을 해당 유저가 좋아요를 눌렀는지 여부
    public boolean isLiked(PostComment postComment, UserEntity UserEntity) {
        if (UserEntity == null || postComment == null) {
            return false;
        }
        return postComment.getLikeUserList().stream()
                .anyMatch(likeUser -> likeUser.equals(UserEntity));
    }

    // 해당 댓글을 해당 유저가 싫어요를 눌렀는지 여부
    public boolean isDisliked(PostComment postComment, UserEntity UserEntity) {
        if (UserEntity == null || postComment == null) {
            return false;
        }
        return postComment.getDislikeUserList().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(UserEntity));
    }

    // 해당 댓글의 작성자인지 여부
    public boolean isCommentMine(PostComment postComment, UserEntity UserEntity) {
        if (UserEntity == null || postComment == null) {
            return false;
        }
        return postComment.getUser().equals(UserEntity);
    }

    // 해당 글을 유저가 좋아요를 눌렀는지의 여부
    public boolean isLiked(PostEntity postEntity, UserEntity UserEntity) {
        if (UserEntity == null || postEntity == null) {
            return false;
        }
        return postEntity.getLikeUserList().stream()
                .anyMatch(likeUser -> likeUser.equals(UserEntity));
    }

    // 해당 글을 해당 유저가 싫어요를 눌렀는지의 여부
    public boolean isScraped(PostEntity postEntity, UserEntity UserEntity) {
        if (UserEntity == null || postEntity == null) {
            return false;
        }
        return postEntity.getDislikeUserList().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(UserEntity));
    }

    // 해당 글의 작성자인지 여부
    public boolean isPostMine(PostEntity postEntity, UserEntity UserEntity) {
        if (UserEntity == null || postEntity == null) {
            return false;
        }
        return postEntity.getUser().equals(UserEntity);
    }

    // flags들 추가하여  PostComment DTO 생성
    public PostCommentDTO createPostCommentDTOWithFlags(PostComment postComment, UserEntity UserEntity) {
        PostCommentDTO postCommentDTO = PostCommentDTO.convertPostCommentToPostCommentDTO(postComment);
        if (UserEntity != null) {
            // 각 댓글에 대해 좋아요, 싫어요, 나의 댓글인지 여부 계산
            boolean isLiked = isLiked(postComment, UserEntity);
            boolean isDisliked = isDisliked(postComment, UserEntity);
            boolean isCommentMine = isCommentMine(postComment, UserEntity);
            postCommentDTO.setIsLiked(isLiked);
            postCommentDTO.setIsDisliked(isDisliked);
            postCommentDTO.setIsCommentMine(isCommentMine);

            // 대댓글 리스트에 대해 재귀적으로 flag 계산
            List<PostCommentDTO> replyDTOs = postComment.getRepliesList().stream()
                    .filter(reply -> reply.getStatus().equals("ACTIVE"))  // 활성화된 대댓글만 필터링
                    .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed()) // 최신순 정렬
                    .map(reply -> createPostCommentDTOWithFlags(reply, UserEntity))  // 재귀적으로 flag 계산
                    .collect(Collectors.toList());

            postCommentDTO.setRepliesList(replyDTOs);

        }

        return postCommentDTO;
    }


    public PostDTO createPostDTOWithFlags(PostEntity postEntity, UserEntity UserEntity) {
        // PostDTO 생성 및 post의 flag 설정
        PostDTO postDTO = PostDTO.convertPostToPostDTO(postEntity);
        if (UserEntity != null) {
            postDTO.setIsPostMine(isPostMine(postEntity, UserEntity));
            postDTO.setIsliked(isLiked(postEntity, UserEntity));
            postDTO.setIsScraped(isScraped(postEntity, UserEntity));
        }
        List<PostCommentDTO> commentDTOList = getPostCommentDTOs(postEntity, UserEntity);

        postDTO.setPostCommentList(commentDTOList);


        return postDTO;
    }

    public List<PostCommentDTO> getPostCommentDTOs(PostEntity postEntity, UserEntity UserEntity) {
        // 각 댓글에 대한 flag 설정
        List<PostCommentDTO> commentDTOList = postEntity.getPostCommentList().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                .filter(comment -> comment.getStatus().equals("ACTIVE")) // 활성화된 댓글만 필터링
                .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed()) // 최신순 정렬
                .map(comment -> createPostCommentDTOWithFlags(comment, UserEntity))
                .collect(Collectors.toList());
        return commentDTOList;
    }

    // 댓글 삭제
    public void deleteComment(Integer commentId, Integer userId) {
        PostComment postComment = getPostCommentByCommentId(commentId);
        postComment.setStatus("DELETED");

        // 대댓글 상태 변경
        postComment.getRepliesList().forEach(reply -> reply.setStatus("DELETED"));
        postCommentApiRepository.save(postComment);
    }

    // 댓글 생성
    public PostComment createComment(String content, String postId, Integer userId) {
        UserEntity UserEntity = userService.findUserById(userId);
        PostEntity postEntity = postApiService.getPost(Integer.valueOf(postId));
        PostComment postComment = new PostComment(content, PostStatus.ACTIVE.name(), LocalDateTime.now(), postEntity, UserEntity);
        return postCommentApiRepository.save(postComment);
    }

    // 대댓글 생성
    public void processParentComment(PostComment postComment, String parentCommentId) {
        PostComment parentComment = getPostCommentByCommentId(Integer.valueOf(parentCommentId));
        postComment.setParentComment(parentComment);
        parentComment.getRepliesList().add(postComment);
        postCommentApiRepository.save(parentComment);
    }

    public int toggleCommentLikeOrDislike(String action, PostComment postComment, UserEntity UserEntity) {
        int commentLikeStatus;
        if ("likes".equals(action)) {
            commentLikeStatus = likeCreateOrDelete(postComment, UserEntity);
        } else if ("dislikes".equals(action)) {
            commentLikeStatus = dislikeCreateOrDelete(postComment, UserEntity);
        } else {
            throw new IllegalArgumentException("action 값이 유효하지 않습니다.");
        }
        return commentLikeStatus;
    }
}
