package com.kustaurant.kustaurant.web.comment;

import com.kustaurant.kustaurant.common.comment.*;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final PostCommentLikesJpaRepository postCommentLikesJpaRepository;
    private final PostCommentDislikesJpaRepository postCommentDislikesJpaRepository;

    // 댓글 생성
    public void create(PostEntity postEntity, User user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        postEntity.getPostCommentList().add(postComment);
        userRepository.save(user);
        postRepository.save(postEntity);
    }

    // 대댓글 생성
    public void replyCreate(User user, PostComment postComment) {
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
    public Map<String, Object> toggleCommentLike(PostComment postComment, User user) {
        Optional<PostCommentLikesEntity> likeOptional = postCommentLikesJpaRepository.findByPostCommentAndUser(postComment, user);
        Optional<PostCommentDislikesEntity> dislikeOptional = postCommentDislikesJpaRepository.findByPostCommentAndUser(postComment, user);
        Map<String, Object> status = new HashMap<>();

        //해당 댓글을 유저가 이미 좋아요를 누른 경우 - 좋아요 제거
        if (likeOptional.isPresent()) {

            PostCommentLikesEntity postCommentLikesEntity = likeOptional.get();
            postCommentLikesJpaRepository.delete(postCommentLikesEntity);
            user.getPostCommentLikesEntities().remove(postCommentLikesEntity);
            postComment.getPostCommentLikesEntities().remove(postCommentLikesEntity);
            postComment.setLikeCount(postComment.getLikeCount() - 1);

            status.put(ReactionStatus.LIKE_DELETED.name(), true);
        }
        //해당 댓글을 유저가 이미 싫어요를 누른 경우 - 제거하고 좋아요 추가
        else if (dislikeOptional.isPresent()) {
            PostCommentDislikesEntity postCommentDislikesEntity = dislikeOptional.get();
            postCommentDislikesJpaRepository.delete(postCommentDislikesEntity);
            user.getPostCommentDislikesEntities().remove(postCommentDislikesEntity);
            postComment.getPostCommentDislikesEntities().remove(postCommentDislikesEntity);

            postComment.setLikeCount(postComment.getLikeCount() + 2);

            PostCommentLikesEntity postCommentLikesEntity = new PostCommentLikesEntity(user,postComment);
            postCommentLikesJpaRepository.save(postCommentLikesEntity);
            postComment.getPostCommentLikesEntities().add(postCommentLikesEntity);
            user.getPostCommentLikesEntities().add(postCommentLikesEntity);

            status.put(ReactionStatus.DISLIKE_TO_LIKE.name(), true);
        }
        // 처음 좋아요 버튼 누른 경우 - 좋아요 추가
        else {
            PostCommentLikesEntity postCommentLikesEntity = new PostCommentLikesEntity(user,postComment);
            postCommentLikesJpaRepository.save(postCommentLikesEntity);
            postComment.getPostCommentLikesEntities().add(postCommentLikesEntity);
            user.getPostCommentLikesEntities().add(postCommentLikesEntity);
            postComment.setLikeCount(postComment.getLikeCount() + 1);

            status.put(ReactionStatus.LIKE_CREATED.name(), true);

        }
        return status;
    }

    // 댓글 싫어요 버튼 토글
    @Transactional
    public Map<String, Object> toggleCommentDislike(PostComment postComment, User user) {

        Optional<PostCommentLikesEntity> likeOptional = postCommentLikesJpaRepository.findByPostCommentAndUser(postComment, user);
        Optional<PostCommentDislikesEntity> dislikeOptional = postCommentDislikesJpaRepository.findByPostCommentAndUser(postComment, user);
        Map<String, Object> status = new HashMap<>();

        //해당 댓글을 유저가 이미 싫어요를 누른 경우 - 싫어요 제거
        if (dislikeOptional.isPresent()) {
            PostCommentDislikesEntity postCommentDislikesEntity = dislikeOptional.get();
            user.getPostCommentDislikesEntities().remove(postCommentDislikesEntity);
            postComment.getPostCommentDislikesEntities().remove(postCommentDislikesEntity);
            postComment.setLikeCount(postComment.getLikeCount() + 1);
            postCommentDislikesJpaRepository.delete(postCommentDislikesEntity);

            status.put(ReactionStatus.DISLIKE_DELETED.name(), true);
        }
        //해당 댓글을 유저가 이미 좋아요 누른 경우 - 좋아요 제거하고 싫어요 추가
        else if (likeOptional.isPresent()) {
            PostCommentLikesEntity postCommentLikesEntity = likeOptional.get();
            user.getPostCommentLikesEntities().remove(postCommentLikesEntity);
            postComment.getPostCommentLikesEntities().remove(postCommentLikesEntity);
            postCommentLikesJpaRepository.delete(postCommentLikesEntity);

            postComment.setLikeCount(postComment.getLikeCount() - 2);

            PostCommentDislikesEntity postCommentDislikesEntity = new PostCommentDislikesEntity(user,postComment);
            postComment.getPostCommentDislikesEntities().add(postCommentDislikesEntity);
            user.getPostCommentDislikesEntities().add(postCommentDislikesEntity);
            postCommentDislikesJpaRepository.save(postCommentDislikesEntity);

            status.put(ReactionStatus.LIKE_TO_DISLIKE.name(), true);
        }
        // 처음 싫어요 버튼 누른 경우 - 싫어요 추가
        else {
            PostCommentDislikesEntity postCommentDislikesEntity = new PostCommentDislikesEntity(user,postComment);
            postComment.getPostCommentDislikesEntities().add(postCommentDislikesEntity);
            user.getPostCommentDislikesEntities().add(postCommentDislikesEntity);
            postComment.setLikeCount(postComment.getLikeCount() - 1);
            postCommentDislikesJpaRepository.save(postCommentDislikesEntity);

            status.put(ReactionStatus.DISLIKE_CREATED.name(), true);
        }
        return status;
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
}
