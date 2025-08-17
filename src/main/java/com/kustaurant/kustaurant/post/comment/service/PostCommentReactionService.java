package com.kustaurant.kustaurant.post.comment.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.comment.controller.response.PostCommReactionResponse;
import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentReactionEntity;
import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommUserReactionId;
import com.kustaurant.kustaurant.post.comment.infrastructure.jpa.PostCommentReactionRepository;
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
    

    public PostCommReactionResponse toggleUserReaction(Integer commentId, Long userId, ReactionType reactionCommand) {
        PostComment comment = postCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUND, "댓글이 존재하지 않습니다."));

        PostCommUserReactionId id = new PostCommUserReactionId(commentId, userId);
        PostCommentReactionEntity existing = reactionRepo.findById(id).orElse(null);

        ReactionType userReaction;
        if (existing == null) {
            reactionRepo.save(PostCommentReactionEntity.of(commentId, userId, reactionCommand));
            userReaction = reactionCommand == ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        } else if (existing.getReaction()==reactionCommand) {
            reactionRepo.delete(existing);
            userReaction = null;
        } else {
            existing.changeTo(reactionCommand);
            userReaction = reactionCommand==ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostCommentIdAndReaction(commentId, ReactionType.DISLIKE);

        return new PostCommReactionResponse(likeCount, dislikeCount, userReaction);
    }
}
