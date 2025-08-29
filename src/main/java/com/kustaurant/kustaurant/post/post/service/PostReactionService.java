package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.infrastructure.PostReactionRepository;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReactionService {
    private final PostReactionRepository reactionRepo;
    public PostReactionResponse toggleLike(Long postId, Long userId, ReactionType reactionCommand) {
        PostUserReactionId id = new PostUserReactionId(postId, userId);
        PostReactionEntity existing = reactionRepo.findById(id).orElse(null);

        ReactionType userReaction;
        if (existing == null) {
            reactionRepo.save(PostReactionEntity.of(postId, userId, reactionCommand));
            userReaction = reactionCommand == ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        } else if (existing.getReaction()==reactionCommand) {
            reactionRepo.delete(existing);
            userReaction = null;
        } else {
            existing.changeTo(reactionCommand);
            userReaction = reactionCommand==ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.DISLIKE);

        return new PostReactionResponse(userReaction, likeCount, dislikeCount, likeCount - dislikeCount);
    }
}
