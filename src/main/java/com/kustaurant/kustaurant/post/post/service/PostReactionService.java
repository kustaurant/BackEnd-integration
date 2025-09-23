package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.common.enums.ReactionType;
import com.kustaurant.kustaurant.post.post.controller.response.PostReactionResponse;
import com.kustaurant.kustaurant.post.post.domain.PostReaction;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.service.port.PostReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostReactionService {
    private final PostReactionRepository reactionRepo;
    public PostReactionResponse setPostReaction(Long postId, Long userId, ReactionType cmd) {
        PostReactionId reactionId = new PostReactionId(postId, userId);
        PostReaction existing = reactionRepo.findById(reactionId).orElse(null);

        if (cmd == null) {
            if (existing != null) reactionRepo.delete(existing);
        } else if (existing == null) { // 기존 반응 없음 -> 신규 생성
            reactionRepo.save(new PostReaction(reactionId, cmd));
        } else if (existing.getReaction() != cmd) { // 변경
            existing.changeTo(cmd);
            reactionRepo.save(existing);
        }

        // 리액션 뒤 최신 지표 조회
        int likeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.LIKE);
        int dislikeCount = reactionRepo.countByPostIdAndReaction(postId, ReactionType.DISLIKE);
        return new PostReactionResponse(cmd, likeCount, dislikeCount, likeCount - dislikeCount);
    }
}
