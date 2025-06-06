package com.kustaurant.kustaurant.web.comment;

import com.kustaurant.kustaurant.common.comment.domain.PostComment;
import com.kustaurant.kustaurant.common.comment.dto.PostCommentDTO;
import com.kustaurant.kustaurant.common.comment.infrastructure.*;
import com.kustaurant.kustaurant.common.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.common.post.domain.*;
import com.kustaurant.kustaurant.common.post.enums.DislikeStatus;
import com.kustaurant.kustaurant.common.post.enums.LikeStatus;
import com.kustaurant.kustaurant.common.post.enums.ReactionStatus;
import com.kustaurant.kustaurant.common.post.enums.ScrapStatus;
import com.kustaurant.kustaurant.common.user.controller.port.UserService;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.web.post.service.PostService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostService postService;
    private final PostCommentLikeJpaRepository postCommentLikeJpaRepository;
    private final PostCommentDislikeJpaRepository postCommentDislikeJpaRepository;
    private final UserService userService;

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

    public List<PostComment> getParentComments(Integer postId, String sort) {
        List<PostComment> postCommentList = postCommentRepository.findParentComments(postId);
        List<PostComment> mutableList = new ArrayList<>(postCommentList);
        if (sort.equals("popular")) {
            mutableList.sort(Comparator.comparingInt(PostComment::getNetLikes).reversed());
        } else {
            mutableList.sort(Comparator.comparing(PostComment::getCreatedAt).reversed());
        }
        return mutableList;
    }



    private Specification<PostCommentEntity> parentAndPostSpec(Integer postId, Specification<PostCommentEntity> baseSpec) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("post").get("postId"), postId),
                cb.isNull(root.get("parentComment")),
                baseSpec == null ? cb.conjunction() : baseSpec.toPredicate(root, query, cb)
        );
    }


    public InteractionStatusResponse getUserInteractionStatus(Integer commentId, Integer userId) {
        if (userId == null) {
            return new InteractionStatusResponse(LikeStatus.NOT_LIKED, DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
        }
        boolean isLiked = postCommentLikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        boolean isDisliked = postCommentDislikeJpaRepository.existsByUser_UserIdAndPostComment_CommentId(userId, commentId);
        return new InteractionStatusResponse(isLiked ? LikeStatus.LIKED : LikeStatus.NOT_LIKED, isDisliked ? DislikeStatus.DISLIKED : DislikeStatus.NOT_DISLIKED, ScrapStatus.NOT_SCRAPPED);
    }

    @Transactional(readOnly = true)
    public PostDetailView buildPostDetailView(Integer postId, Integer userId, String sort) {
        User currentUser = (userId != null) ? userService.getActiveUserById(userId) : null;

        // 1. 게시글 정보
        Post post = postService.getPost(postId);
        User postAuthor = userService.getActiveUserById(post.getAuthorId());
        PostDTO postDTO = PostDTO.from(post, postAuthor);

        // 2. 부모 댓글+대댓글 트리 조회
        List<PostComment> parentComments = getParentComments(postId, sort);

        // 3. 댓글 상호작용 맵 생성
        Map<Integer, InteractionStatusResponse> commentInteractionMap = new HashMap<>();
        for (PostComment comment : parentComments) {
            fillInteractionMap(comment, userId, commentInteractionMap);
        }

        // 4. 댓글 작성자 id 모두 조회
        List<Integer> allUserIds = parentComments.stream()
                .flatMap(c -> collectAllReplies(c).stream())
                .map(PostComment::getUserId)
                .distinct()
                .toList();

        // 5. id -> UserDTO 매핑
        Map<Integer, UserDTO> userDtoMap = userService.getUserDTOsByIds(allUserIds);

        // 6. 댓글 트리 → DTO 변환
        List<PostCommentDTO> commentDTOs = parentComments.stream()
                .filter(c -> c.getParentComment() == null)
                .map(c -> {
                    Integer commentId = c.getCommentId();
                    Integer authorId = c.getUserId();
                    PostCommentDTO dto = PostCommentDTO.from(c, userDtoMap);

                    InteractionStatusResponse res = commentInteractionMap.get(commentId);
                    if (res != null) {
                        dto.setIsLiked(res.getLiked().isLiked());
                        dto.setIsDisliked(res.getDisliked().isDisliked());
                    }

                    dto.setIsCommentMine(currentUser != null && authorId.equals(currentUser.getId()));
                    return dto;
                }).toList();

        postDTO.setPostCommentList(commentDTOs);

        // 7. 최종 뷰 조합
        return PostDetailView.builder()
                .post(postDTO)
                .postInteractionStatus(postService.getUserInteractionStatus(postId, userId))
                .commentInteractionMap(commentInteractionMap)
                .sort(sort)
                .build();
    }

    private List<PostComment> collectAllReplies(PostComment comment) {
        List<PostComment> all = new ArrayList<>();
        all.add(comment); // 자기 자신 추가
        for (PostComment reply : comment.getReplies()) {
            all.addAll(collectAllReplies(reply)); // 자식의 자식까지 재귀적으로 추가
        }
        return all;
    }

    public void fillInteractionMap(PostComment comment, Integer userId, Map<Integer, InteractionStatusResponse> map) {
        map.put(comment.getCommentId(), getUserInteractionStatus(comment.getCommentId(), userId));
        for (PostComment reply : comment.getReplies()) {
            fillInteractionMap(reply, userId, map);
        }
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
