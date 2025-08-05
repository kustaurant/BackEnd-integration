package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.infrastructure.PostUserReactionRepository;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostUserReactionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReactionService {
    private final PostUserReactionRepository reactionRepo;
    public PostReactionResponse toggleLike(Integer postId, Long userId, ReactionType reactionType) {
        PostUserReactionId id = new PostUserReactionId(postId, userId);
        PostUserReactionEntity existing = reactionRepo.findById(id).orElse(null);

        ReactionType reaction;
        if (existing == null) {
            reactionRepo.save(PostUserReactionEntity.of(postId, userId, reactionType));
            reaction = reactionType == ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        } else if (existing.getReaction()==reactionType) {
            reactionRepo.delete(existing);
            reaction = null;
        } else {
            existing.changeTo(reactionType);
            reaction = reactionType==ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.DISLIKE);

        return new PostReactionResponse(reaction, likeCount - dislikeCount, likeCount, dislikeCount);
    }
}
