package com.kustaurant.kustaurant.post.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReaction;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReactionId;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentReactionRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.COMMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PostCommentReactionService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentReactionRepository reactionRepo;

    public PostCommReactionResponse setPostCommentReaction(Long commentId, Long userId, @Nullable ReactionType cmd) {
        postCommentRepository.findById(commentId).orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "댓글이 존재하지 않습니다."));

        PostCommentReactionId reactionId = new PostCommentReactionId(commentId, userId);
        PostCommentReaction existing = reactionRepo.findById(reactionId).orElse(null);

        if (cmd == null) { // 해제
            if (existing != null) reactionRepo.deleteById(reactionId);
        } else if (existing == null) { // 신규
            try {
                reactionRepo.save(new PostCommentReaction(reactionId, cmd));
            } catch (DataIntegrityViolationException e) {
                // 경쟁 상태에서 이미 들어간 경우 -> 멱등 처리
            }
        } else if (existing.getReaction() != cmd) { // 변경
            existing.changeTo(cmd);
            reactionRepo.save(existing);
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.DISLIKE);

        return new PostCommReactionResponse(likeCount, dislikeCount, cmd);
    }
}
