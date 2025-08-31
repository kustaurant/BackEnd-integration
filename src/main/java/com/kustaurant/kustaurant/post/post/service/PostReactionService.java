package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostReactionJpaId;
import com.kustaurant.kustaurant.post.post.service.port.PostReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostReactionService {
    private final PostReactionRepository reactionRepo;
    public PostReactionResponse toggleLike(Long postId, Long userId, ReactionType cmd) {
        PostReactionId reactionId = new PostReactionId(postId, userId);
        PostReaction existing = reactionRepo.findById(reactionId).orElse(null);

        ReactionType userReaction;
        if (existing == null) { // 기존 반응 없음 -> 신규 생성
            reactionRepo.save(new PostReaction(reactionId, cmd));
            userReaction = cmd == ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        } else if (existing.getReaction()==cmd) { // 동일 반응 -> 기존값 제거
            reactionRepo.delete(reactionId);
            userReaction = null;
        } else { // 다른 반응 -> 다른 반응으로 변경
            existing.changeTo(cmd);
            reactionRepo.save(existing);
            userReaction = cmd==ReactionType.LIKE ? ReactionType.LIKE : ReactionType.DISLIKE;
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.DISLIKE);

        return new PostReactionResponse(userReaction, likeCount, dislikeCount, likeCount - dislikeCount);
    }
}
