package com.kustaurant.restauranttier.tab4_community.service;


import com.kustaurant.restauranttier.common.exception.exception.OptionalNotExistException;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.repository.PostCommentApiRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostApiRepository;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PostApiCommentService {
    private final PostCommentApiRepository postCommentApiRepository;
    private final UserRepository userRepository;
    private final PostApiRepository postApiRepository;
    private final PostApiService postApiService;

    // 댓글 생성
    public void create(Post post, User user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        post.getPostCommentList().add(postComment);
        userRepository.save(user);
        postApiRepository.save(post);
    }

    // 대댓글 생성
    public void replyCreate(User user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        userRepository.save(user);
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
        List<User> likeUserList = postComment.getLikeUserList();
        List<User> dislikeUserList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = user.getDislikePostCommentList();
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
        List<User> likeUserList = postComment.getLikeUserList();
        List<User> dislikeUserList = postComment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = user.getDislikePostCommentList();
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


    public List<PostComment> getList(Integer postId, String sort) {
        Post post = postApiService.getPost(postId);
        Specification<PostComment> spec = getSpecByPostId(post);
        List<PostComment> postCommentList = postCommentApiRepository.findAll(spec);
        if (sort.equals("popular")) {
            postCommentList.sort(Comparator.comparingInt(PostComment::getLikeCount).reversed());
        } else {
            postCommentList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());

        }
        return postCommentList;
    }

    private Specification<PostComment> getSpecByPostId(Post post) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<PostComment> p, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Predicate postIdPredicate = cb.equal(p.get("post"), post);
                Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");


                return cb.and(statusPredicate, postIdPredicate);


            }


        };
    }
}
