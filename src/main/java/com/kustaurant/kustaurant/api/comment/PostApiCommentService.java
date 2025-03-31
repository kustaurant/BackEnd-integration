package com.kustaurant.kustaurant.api.comment;


import com.kustaurant.kustaurant.api.post.service.PostApiService;
import com.kustaurant.kustaurant.common.comment.PostComment;
import com.kustaurant.kustaurant.common.comment.PostCommentApiRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.global.UserService;
import com.kustaurant.kustaurant.global.exception.exception.OptionalNotExistException;
import com.kustaurant.kustaurant.common.post.domain.PostDTO;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.comment.PostCommentDTO;
import com.kustaurant.kustaurant.common.post.enums.PostStatus;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostApiCommentService {
    private final PostCommentApiRepository postCommentApiRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostApiService postApiService;
    private final UserService userService;

    // 댓글 생성
    public void create(PostEntity postEntity, User user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        postEntity.getPostCommentList().add(postComment);
        userRepository.save(user);
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
    public int likeCreateOrDelete(PostComment postComment, User user) {
        List<User> likeUserList = postComment.getPostCommentLikesEntities();
        List<User> dislikeUserList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getPostCommentLikesEntities();
        List<PostComment> dislikePostCommentList = user.getPostCommentDislikesEntities();
        int commentLikeStatus;

        // 해당 postComment를 like 한 경우 - 제거
        if (likeUserList.contains(user)) {
            postComment.setLikeCount(postComment.getLikeCount() - 1);
            likePostCommentList.remove(postComment);
            likeUserList.remove(user);
            commentLikeStatus = 0; // 아무것도 누르지 않은 상태로 변경
        }
        // 해당 postComment를 이미 dislike 한 경우 - 제거하고 like 추가
        else if (dislikeUserList.contains(user)) {
            postComment.setLikeCount(postComment.getLikeCount() + 2);
            dislikeUserList.remove(user);
            dislikePostCommentList.remove(postComment);
            likeUserList.add(user);
            likePostCommentList.add(postComment);
            commentLikeStatus = 1; // 좋아요 상태로 변경
        }
        // 처음 like 하는 경우 - 추가
        else {
            postComment.setLikeCount(postComment.getLikeCount() + 1);
            likeUserList.add(user);
            likePostCommentList.add(postComment);
            commentLikeStatus = 1; // 좋아요 상태로 변경
        }

        postCommentApiRepository.save(postComment);
        userRepository.save(user);

        return commentLikeStatus;
    }

    // 댓글 싫어요 (세 가지 경우)
    public int dislikeCreateOrDelete(PostComment postComment, User user) {
        List<User> likeUserList = postComment.getPostCommentLikesEntities();
        List<User> dislikeUserList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getPostCommentLikesEntities();
        List<PostComment> dislikePostCommentList = user.getPostCommentDislikesEntities();
        int commentLikeStatus;

        // 해당 postComment를 dislike 한 경우 - 제거
        if (dislikeUserList.contains(user)) {
            postComment.setLikeCount(postComment.getLikeCount() + 1); // 싫어요를 제거하므로 좋아요 수 증가
            dislikePostCommentList.remove(postComment);
            dislikeUserList.remove(user);
            commentLikeStatus = 0; // 아무것도 누르지 않은 상태로 변경
        }
        // 해당 postComment를 이미 like 한 경우 - 제거하고 dislike 추가
        else if (likeUserList.contains(user)) {
            postComment.setLikeCount(postComment.getLikeCount() - 2);
            likeUserList.remove(user);
            likePostCommentList.remove(postComment);
            dislikeUserList.add(user);
            dislikePostCommentList.add(postComment);
            commentLikeStatus = -1; // 싫어요 상태로 변경
        }
        // 처음 dislike 하는 경우 - 추가
        else {
            postComment.setLikeCount(postComment.getLikeCount() - 1);
            dislikeUserList.add(user);
            dislikePostCommentList.add(postComment);
            commentLikeStatus = -1; // 싫어요 상태로 변경
        }

        postCommentApiRepository.save(postComment);
        userRepository.save(user);

        return commentLikeStatus;
    }


    // 해당 댓글을 해당 유저가 좋아요를 눌렀는지 여부
    public boolean isLiked(PostComment postComment, User user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getPostCommentLikesEntities().stream()
                .anyMatch(likeUser -> likeUser.equals(user));
    }

    // 해당 댓글을 해당 유저가 싫어요를 눌렀는지 여부
    public boolean isDisliked(PostComment postComment, User user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getDislikeUserList().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(user));
    }

    // 해당 댓글의 작성자인지 여부
    public boolean isCommentMine(PostComment postComment, User user) {
        if (user == null || postComment == null) {
            return false;
        }
        return postComment.getUser().equals(user);
    }

    // 해당 글을 유저가 좋아요를 눌렀는지의 여부
    public boolean isLiked(PostEntity postEntity, User user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostLikesList().stream()
                .anyMatch(postLikesEntity -> postLikesEntity.getUser().equals(user));
    }

    // 해당 글을 해당 유저가 싫어요를 눌렀는지의 여부
    public boolean isScraped(PostEntity postEntity, User user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getPostDislikesList().stream()
                .anyMatch(dislikeUser -> dislikeUser.equals(user));
    }

    // 해당 글의 작성자인지 여부
    public boolean isPostMine(PostEntity postEntity, User user) {
        if (user == null || postEntity == null) {
            return false;
        }
        return postEntity.getUser().equals(user);
    }

    // flags들 추가하여  PostComment DTO 생성
    public PostCommentDTO createPostCommentDTOWithFlags(PostComment postComment, User user) {
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
                    .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed()) // 최신순 정렬
                    .map(reply -> createPostCommentDTOWithFlags(reply, user))  // 재귀적으로 flag 계산
                    .collect(Collectors.toList());

            postCommentDTO.setRepliesList(replyDTOs);

        }

        return postCommentDTO;
    }


    public PostDTO createPostDTOWithFlags(PostEntity postEntity, User user) {
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

    public List<PostCommentDTO> getPostCommentDTOs(PostEntity postEntity, User user) {
        // 각 댓글에 대한 flag 설정
        List<PostCommentDTO> commentDTOList = postEntity.getPostCommentList().stream()
                .filter(comment -> comment.getParentComment() == null) // 부모 댓글만 처리
                .filter(comment -> comment.getStatus().equals("ACTIVE")) // 활성화된 댓글만 필터링
                .sorted(Comparator.comparing(PostComment::getCreatedAt).reversed()) // 최신순 정렬
                .map(comment -> createPostCommentDTOWithFlags(comment, user))
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
        User user = userService.findUserById(userId);
        PostEntity postEntity = postApiService.getPost(Integer.valueOf(postId));
        PostComment postComment = new PostComment(content, PostStatus.ACTIVE.name(), LocalDateTime.now(), postEntity, user);
        return postCommentApiRepository.save(postComment);
    }

    // 대댓글 생성
    public void processParentComment(PostComment postComment, String parentCommentId) {
        PostComment parentComment = getPostCommentByCommentId(Integer.valueOf(parentCommentId));
        postComment.setParentComment(parentComment);
        parentComment.getRepliesList().add(postComment);
        postCommentApiRepository.save(parentComment);
    }

    public int toggleCommentLikeOrDislike(String action, PostComment postComment, User user) {
        int commentLikeStatus;
        if ("likes".equals(action)) {
            commentLikeStatus = likeCreateOrDelete(postComment, user);
        } else if ("dislikes".equals(action)) {
            commentLikeStatus = dislikeCreateOrDelete(postComment, user);
        } else {
            throw new IllegalArgumentException("action 값이 유효하지 않습니다.");
        }
        return commentLikeStatus;
    }
}
