package com.kustaurant.restauranttier.tab4_community.service;

import com.kustaurant.restauranttier.common.exception.exception.DataNotFoundException;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.repository.PostCommentRepository;
import com.kustaurant.restauranttier.tab4_community.repository.PostRepository;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
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
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    // 댓글 생성
    public void create(Post post, User user, PostComment postComment) {
        user.getPostCommentList().add(postComment);
        post.getPostCommentList().add(postComment);
        userRepository.save(user);
        postRepository.save(post);
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

//    // 작성 시간 반환
//    public List<String> getCreatedAtList(List<PostComment> postCommentList) {
//        // Comment의 createdAt을 문자열로 변환하여 저장할 리스트
//        List<String> commentCreatedAtList = postCommentList.stream()
//                .map(comment -> formatDateTime(comment.getCreatedAt()))
//                .collect(Collectors.toList());
//        return commentCreatedAtList;
//    }
//
//    // datetime 타입의 시간을 특정 형식으로 formatting하는 함수
//    private String formatDateTime(LocalDateTime dateTime) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        return dateTime.format(formatter);
//    }

    // 댓글 좋아요 (세가지 경우)
    public Map<String, Object> likeCreateOrDelete(PostComment postcomment, User user) {
        List<User> likeUserList = postcomment.getLikeUserList();
        List<User> dislikeUserList = postcomment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = user.getDislikePostCommentList();
        Map<String, Object> status = new HashMap<>();
        //해당 postcomment를 like 한 경우 - 제거
        if (likeUserList.contains(user)) {
            postcomment.setLikeCount(postcomment.getLikeCount() - 1);
            likePostCommentList.remove(postcomment);
            likeUserList.remove(user);
            status.put("likeDelete", true);
        }
        //해당 postcomment를 이미 dislike 한 경우 - 제거하고  like 추가
        else if (dislikeUserList.contains(user)) {
            postcomment.setLikeCount(postcomment.getLikeCount() + 2);
            dislikeUserList.remove(user);
            dislikePostCommentList.remove(postcomment);
            likeUserList.add(user);
            likePostCommentList.add(postcomment);
            status.put("changeToLike", true);

        }
        // 처음 dislike 하는 경우-추가
        else {
            postcomment.setLikeCount(postcomment.getLikeCount() + 1);
            likeUserList.add(user);
            likePostCommentList.add(postcomment);
            status.put("likeCreated", true);

        }
        postCommentRepository.save(postcomment);
        userRepository.save(user);
        return status;
    }

    // 댓글 싫어요 (세가지 경우)
    public Map<String, Object> dislikeCreateOrDelete(PostComment postcomment, User user) {
        List<User> likeUserList = postcomment.getLikeUserList();
        List<User> dislikeUserList = postcomment.getDislikeUserList();
        List<PostComment> likePostCommentList = user.getLikePostCommentList();
        List<PostComment> dislikePostCommentList = user.getDislikePostCommentList();
        Map<String, Object> status = new HashMap<>();

        //해당 post를 이미 dislike 한 경우 - 제거
        if (dislikeUserList.contains(user)) {
            postcomment.setLikeCount(postcomment.getLikeCount() + 1);

            dislikePostCommentList.remove(postcomment);
            dislikeUserList.remove(user);
            status.put("dislikeDelete", true);
        }
        //해당 post를 이미 like 한 경우 - 제거하고 추가
        else if (likeUserList.contains(user)) {
            postcomment.setLikeCount(postcomment.getLikeCount() - 2);

            likeUserList.remove(user);
            likePostCommentList.remove(postcomment);
            dislikeUserList.add(user);
            dislikePostCommentList.add(postcomment);
            status.put("changeToDislike", true);
        }
        // 처음 dislike 하는 경우-추가
        else {
            postcomment.setLikeCount(postcomment.getLikeCount() - 1);

            dislikeUserList.add(user);
            dislikePostCommentList.add(postcomment);
            status.put("dislikeCreated", true);
        }
        postCommentRepository.save(postcomment);
        userRepository.save(user);
        return status;
    }


    public List<PostComment> getList(Integer postId, String sort) {
        Post post = postService.getPost(postId);
        Specification<PostComment> spec = getSpecByPostId(post);
        List<PostComment> postCommentList = postCommentRepository.findAll(spec);
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
