package com.kustaurant.kustaurant.post.comment.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.comment.controller.request.PostCommentRequest;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommentDeleteResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentStatus;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.mypage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;

    private final UserStatsService userStatsService;

    public PostComment create(Long postId, PostCommentRequest req, Long userId) {
        postRepository.findById(postId).orElseThrow(()->new DataNotFoundException(POST_NOT_FOUND));
        if (req.parentCommentId() != null) {
            postCommentRepository.findById(req.parentCommentId())
                    .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "부모 댓글을 찾을 수 없습니다."));
        }

        userStatsService.incPostComment(userId);
        return postCommentRepository.save(PostComment.create(postId, req, userId));
    }

    public PostCommentDeleteResponse delete(Long commentId, Long userId) {
        // 댓글 조회 및 권한 검증
        PostComment comment = postCommentRepository.findByIdForUpdate(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, commentId, "댓글"));
        comment.ensureWriterBy(userId);

        // 응답 빌드 준비
        Long parentId = comment.getParentCommentId();
        Long postId = comment.getPostId();
        List<Long> removedIds = new ArrayList<>();
        PostCommentStatus finalStatus;

        // 대댓글인 경우 대댓글은 즉시삭제
        if (comment.isReplyComment()) {
            postCommentRepository.delete(comment);
            removedIds.add(comment.getId());
            finalStatus = PostCommentStatus.DELETED;
            // 부모 댓글이 PENDING 상태인지 확인하고 필요시 삭제
            checkAndDeleteParentIfNeeded(parentId, removedIds);
        } else { // 부모 댓글인 경우
            long activeReplyCount = postCommentRepository.countActiveRepliesByParentCommentId(comment.getId());

            if (comment.hasActiveReplies(activeReplyCount)) {
                // 활성 대댓글이 있으면 PENDING 삭제
                comment.pendingDelete();
                postCommentRepository.save(comment);
                finalStatus = PostCommentStatus.PENDING;
            } else {
                // 활성 대댓글이 없으면 삭제
                postCommentRepository.delete(comment);
                removedIds.add(comment.getId());
                finalStatus = PostCommentStatus.DELETED;
            }
        }
        long totalVisible = postCommentRepository.countVisibleRepliesByPostId(postId);

        userStatsService.decPostComment(userId);
        return new PostCommentDeleteResponse(
                comment.getId(),
                parentId,
                finalStatus,
                removedIds,
                totalVisible
        );
    }
    
    private void checkAndDeleteParentIfNeeded(Long parentId, List<Long> removedIds) {
        PostComment parent = postCommentRepository.findById(parentId).orElse(null);
        
        if (parent != null && parent.getStatus() == PostCommentStatus.PENDING) {
            long remainingActiveReplies = postCommentRepository.countActiveRepliesByParentCommentId(parentId);
            if (remainingActiveReplies == 0) {
                // 더 이상 활성 대댓글이 없으면 부모 댓글도 삭제
                postCommentRepository.delete(parent);
                removedIds.add(parent.getId());
            }
        }
    }
}
