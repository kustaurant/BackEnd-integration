package com.kustaurant.kustaurant.web.comment;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.infrastructure.*;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.common.post.domain.InteractionStatusResponse;
import com.kustaurant.kustaurant.common.post.domain.Post;
import com.kustaurant.kustaurant.common.post.domain.ReactionToggleResponse;
import com.kustaurant.kustaurant.common.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.common.post.enums.LikeStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.enums.ScrapStatus;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.web.post.service.PostService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostService postService;
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createComment(String content, Integer postId, Integer parentCommentId, Integer userId) {
        PostComment comment = PostComment.create(content, userId, postId);
        if (parentCommentId != null) {
            PostComment parent = postCommentRepository.findById(parentCommentId)
                    .orElseThrow(() -> new DataNotFoundException("부모 댓글을 찾을 수 없습니다."));
            comment.setParent(parent);
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
            throw new DataNotFoundException("PostComment not found");
        }
    }

    @Transactional
    public ReactionToggleResponse toggleLike(Integer userId, Integer commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("댓글이 존재하지 않습니다."));
        boolean isLikedBefore = postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        boolean isDislikedBefore = postCommentDislikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        ReactionStatus status = comment.toggleLike(isLikedBefore,isDislikedBefore);
        postCommentRepository.save(comment);

        return new ReactionToggleResponse(status, comment.getNetLikes(), comment.getLikeCount(), comment.getDislikeCount());
    }

    @Transactional
    public ReactionToggleResponse toggleDislike(Integer userId, Integer commentId) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("댓글이 존재하지 않습니다."));
        boolean isLikedBefore = postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        boolean isDislikedBefore = postCommentDislikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);

        ReactionStatus status = comment.toggleDislike(isLikedBefore, isDislikedBefore);
        postCommentRepository.save(comment);

        return new ReactionToggleResponse(status, comment.getNetLikes(), comment.getLikeCount(), comment.getDislikeCount());
    }

    private void addCommentLike(UserEntity user, PostCommentEntity postComment) {
        PostCommentLikeEntity postCommentLikeEntity = new PostCommentLikeEntity(user, postComment);
        postCommentLikeJpaRepository.save(postCommentLikeEntity);
        postComment.getPostCommentLikesEntities().add(postCommentLikeEntity);
        user.getPostCommentLikesEntities().add(postCommentLikeEntity);
    }

    private void addCommentDislike(UserEntity user, PostCommentEntity comment) {
        PostCommentDislikeEntity dislike = new PostCommentDislikeEntity(user, comment);
        user.getPostCommentDislikesEntities().add(dislike);
        comment.getPostCommentDislikesEntities().add(dislike);
        postCommentDislikeJpaRepository.save(dislike);
    }

    private void removeCommentLike(UserEntity user, PostCommentEntity comment, PostCommentLikeEntity like) {
        user.getPostCommentLikesEntities().remove(like);
        comment.getPostCommentLikesEntities().remove(like);
        postCommentLikeJpaRepository.delete(like);
    }

    private void removeCommentDislike(UserEntity user, PostCommentEntity postComment, PostCommentDislikeEntity postCommentDislikeEntity) {
        postCommentDislikeJpaRepository.delete(postCommentDislikeEntity);
        user.getPostCommentDislikesEntities().remove(postCommentDislikeEntity);
        postComment.getPostCommentDislikesEntities().remove(postCommentDislikeEntity);
    }

    public List<PostComment> getList(Integer postId, String sort) {
        Post post = postService.getPost(postId);
        Specification<PostComment> spec = getSpecByPostId(post);
        List<PostComment> postCommentList = postCommentRepository.findAll(spec);

        if (sort.equals("popular")) {
            postCommentList.sort(Comparator.comparingInt(PostComment::getNetLikes).reversed());
        } else {
            postCommentList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());

        }
        return postCommentList;
    }

    private Specification<PostComment> getSpecByPostId(Post post) {
        return (p, query, cb) -> {
            query.distinct(true);  // 중복을 제거
            Predicate postIdPredicate = cb.equal(p.get("post").get("postId"), post.getId());
            Predicate statusPredicate = cb.equal(p.get("status"), "ACTIVE");

            return cb.and(statusPredicate, postIdPredicate);
        };
    }

    public InteractionStatusResponse getUserInteractionStatus(Integer commentId, Integer userId) {
        if (userId == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }
        boolean isLiked = postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        boolean isDisliked = postCommentDislikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
    }

    public Map<Integer, InteractionStatusResponse> getCommentInteractionMap(List<PostComment> postCommentList, Integer userId) {

        Map<Integer, InteractionStatusResponse> commentInteractionMap = new HashMap<>();

        for (PostComment comment : postCommentList) {
            // 댓글
            commentInteractionMap.put(comment.getCommentId(), getUserInteractionStatus(comment.getCommentId(), userId));

            // 대댓글
            for (PostComment reply : comment.getReplies()) {
                commentInteractionMap.put(reply.getCommentId(), getUserInteractionStatus(reply.getCommentId(), userId));
            }
        }
        return commentInteractionMap;
    }

    @Transactional
    public int deleteComment(Integer commentId) {
        PostComment comment = postCommentRepository.findByIdWithReplies(commentId)
                .orElseThrow(() -> new DataNotFoundException("댓글이 존재하지 않습니다."));

        comment.delete();  // 도메인 내에서 댓글 + 대댓글 상태 변경

        postCommentRepository.save(comment); // 변경 반영

        return 1 + comment.getReplies().size(); // 삭제된 댓글 수 리턴
    }

}
