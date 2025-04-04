package com.kustaurant.kustaurant.web.comment;

import com.kustaurant.kustaurant.common.comment.*;
import com.kustaurant.kustaurant.common.post.domain.InteractionStatusResponse;
import com.kustaurant.kustaurant.common.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.common.post.enums.LikeStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.enums.ScrapStatus;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.web.post.service.PostService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final OUserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;

    // 댓글 생성
    public void create(PostEntity postEntity, UserEntity user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        postEntity.getPostCommentList().add(postComment);
        userRepository.save(user);
        postRepository.save(postEntity);
    }

    // 대댓글 생성
    public void replyCreate(UserEntity user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        userRepository.save(user);
    }

    // 댓글 조회
    public PostComment getPostCommentByCommentId(Integer commentId) {
        Optional<PostComment> postComment = postCommentRepository.findById(commentId);
        if (postComment.isPresent()) {
            return postComment.get();
        } else {
            throw new DataNotFoundException("PostComment not found");
        }
    }

    // 댓글 좋아요 토글 버튼
    @Transactional
    public Map<String, Object> toggleCommentLike(PostComment postComment, UserEntity user) {
        Optional<PostCommentLikeEntity> likeOptional = postCommentLikeJpaRepository.findByUserAndPostComment(user, postComment);
        Optional<PostCommentDislikeEntity> dislikeOptional = postCommentDislikeJpaRepository.findByUserAndPostComment(user, postComment);
        Map<String, Object> status = new HashMap<>();

        //해당 댓글을 유저가 이미 좋아요를 누른 경우 - 좋아요 제거
        if (likeOptional.isPresent()) {
            PostCommentLikeEntity postCommentLikeEntity = likeOptional.get();
            removeCommentLike(user, postComment, postCommentLikeEntity);
            postComment.setLikeCount(postComment.getLikeCount() - 1);

            status.put(ReactionStatus.LIKE_DELETED.name(), true);
        }
        //해당 댓글을 유저가 이미 싫어요를 누른 경우 - 싫어요 제거하고 좋아요 추가
        else if (dislikeOptional.isPresent()) {
            PostCommentDislikeEntity postCommentDislikeEntity = dislikeOptional.get();
            removeCommentDislike(user, postComment, postCommentDislikeEntity);
            addCommentLike(user, postComment);
            postComment.setLikeCount(postComment.getLikeCount() + 2);

            status.put(ReactionStatus.DISLIKE_TO_LIKE.name(), true);
        }
        // 처음 좋아요 버튼 누른 경우 - 좋아요 추가
        else {
            addCommentLike(user, postComment);
            postComment.setLikeCount(postComment.getLikeCount() + 1);

            status.put(ReactionStatus.LIKE_CREATED.name(), true);
        }
        return status;
    }

    // 댓글 싫어요 버튼 토글
    @Transactional
    public Map<String, Object> toggleCommentDislike(PostComment postComment, UserEntity user) {

        Optional<PostCommentLikeEntity> likeOptional = postCommentLikeJpaRepository.findByUserAndPostComment(user, postComment);
        Optional<PostCommentDislikeEntity> dislikeOptional = postCommentDislikeJpaRepository.findByUserAndPostComment(user, postComment);
        Map<String, Object> status = new HashMap<>();

        //해당 댓글을 유저가 이미 싫어요를 누른 경우 - 싫어요 제거
        if (dislikeOptional.isPresent()) {
            PostCommentDislikeEntity postCommentDislikeEntity = dislikeOptional.get();
            removeCommentDislike(user, postComment, postCommentDislikeEntity);
            postComment.setLikeCount(postComment.getLikeCount() + 1);

            status.put(ReactionStatus.DISLIKE_DELETED.name(), true);
        }
        //해당 댓글을 유저가 이미 좋아요 누른 경우 - 좋아요 제거하고 싫어요 추가
        else if (likeOptional.isPresent()) {
            PostCommentLikeEntity postCommentLikeEntity = likeOptional.get();
            removeCommentLike(user, postComment, postCommentLikeEntity);
            addCommentDislike(user, postComment);
            postComment.setLikeCount(postComment.getLikeCount() - 2);

            status.put(ReactionStatus.LIKE_TO_DISLIKE.name(), true);
        }
        // 처음 싫어요 버튼 누른 경우 - 싫어요 추가
        else {
            addCommentDislike(user, postComment);
            postComment.setLikeCount(postComment.getLikeCount() - 1);

            status.put(ReactionStatus.DISLIKE_CREATED.name(), true);
        }
        return status;
    }

    private void addCommentLike(UserEntity user, PostComment postComment) {
        PostCommentLikeEntity postCommentLikeEntity = new PostCommentLikeEntity(user, postComment);
        postCommentLikeJpaRepository.save(postCommentLikeEntity);
        postComment.getPostCommentLikesEntities().add(postCommentLikeEntity);
        user.getPostCommentLikesEntities().add(postCommentLikeEntity);
    }

    private void addCommentDislike(UserEntity user, PostComment comment) {
        PostCommentDislikeEntity dislike = new PostCommentDislikeEntity(user, comment);
        user.getPostCommentDislikesEntities().add(dislike);
        comment.getPostCommentDislikesEntities().add(dislike);
        postCommentDislikeJpaRepository.save(dislike);
    }

    private void removeCommentLike(UserEntity user, PostComment comment, PostCommentLikeEntity like) {
        user.getPostCommentLikesEntities().remove(like);
        comment.getPostCommentLikesEntities().remove(like);
        postCommentLikeJpaRepository.delete(like);
    }

    private void removeCommentDislike(UserEntity user, PostComment postComment, PostCommentDislikeEntity postCommentDislikeEntity) {
        postCommentDislikeJpaRepository.delete(postCommentDislikeEntity);
        user.getPostCommentDislikesEntities().remove(postCommentDislikeEntity);
        postComment.getPostCommentDislikesEntities().remove(postCommentDislikeEntity);
    }

    public List<PostComment> getList(Integer postId, String sort) {
        PostEntity postEntity = postService.getPost(postId);
        Specification<PostComment> spec = getSpecByPostId(postEntity);
        List<PostComment> postCommentList = postCommentRepository.findAll(spec);
        if (sort.equals("popular")) {
            postCommentList.sort(Comparator.comparingInt(PostComment::getLikeCount).reversed());
        } else {
            postCommentList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());

        }
        return postCommentList;
    }

    private Specification<PostComment> getSpecByPostId(PostEntity postEntity) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostComment> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Predicate postIdPredicate = cb.equal(p.get("postEntity"), postEntity);
                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");

                return cb.and(statusPredicate, postIdPredicate);
            }
        };
    }

    public InteractionStatusResponse getUserInteractionStatus(PostComment postComment, UserEntity user) {
        if (user == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }
        boolean isLiked = postCommentLikeJpaRepository.existsByUserAndPostComment(user, postComment);
        boolean isDisliked = postCommentDislikeJpaRepository.existsByUserAndPostComment(user, postComment);
        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
    }

    public Map<Integer, InteractionStatusResponse> getCommentInteractionMap(List<PostComment> postCommentList, UserEntity user) {
        Map<Integer, InteractionStatusResponse> commentInteractionMap = new HashMap<>();

        for (PostComment comment : postCommentList) {
            // 댓글
            commentInteractionMap.put(comment.getCommentId(), getUserInteractionStatus(comment, user));

            // 대댓글
            for (PostComment reply : comment.getRepliesList()) {
                commentInteractionMap.put(reply.getCommentId(), getUserInteractionStatus(reply, user));
            }
        }
        return commentInteractionMap;
    }
}
