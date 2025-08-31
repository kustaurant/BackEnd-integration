package com.kustaurant.kustaurant.post.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReaction;
import com.kustaurant.kustaurant.post.comment.domain.PostCommentReactionId;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentReactionRepository;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.COMMENT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class PostCommentReactionService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentReactionRepository reactionRepo;

    public PostCommReactionResponse toggleUserReaction(Long commentId, Long userId, ReactionType reactionCommand) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "댓글이 존재하지 않습니다."));

        PostCommentReactionId reactionId = new PostCommentReactionId(commentId, userId);
        PostCommentReaction existing = reactionRepo.findById(reactionId).orElse(null);

        ReactionType userReaction;
        if (existing == null) { // 기존 반응 없음 -> 신규 생성
            reactionRepo.save(new PostCommentReaction(reactionId, reactionCommand));
            userReaction = reactionCommand == ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        } else if (existing.getReaction()==reactionCommand) { // 동일 반응 -> 기존값 제거
            reactionRepo.delete(reactionId);
            userReaction = null;
        } else { // 다른 반응 -> 다른 반응으로 변경
            existing.changeTo(reactionCommand);
            reactionRepo.save(existing);
            userReaction = reactionCommand==ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.DISLIKE);

        return new PostCommReactionResponse(likeCount, dislikeCount, userReaction);
    }
}
