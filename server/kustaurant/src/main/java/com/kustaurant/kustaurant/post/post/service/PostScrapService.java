package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.controller.response.PostScrapResponse;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.user.mypage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    private final UserStatsService userStatsService;

    @Transactional
    public PostScrapResponse toggleScrapWithCount(Long postId, Long userId, boolean cmd) {
        postRepository.findById(postId).orElseThrow(
                ()-> new DataNotFoundException(ErrorCode.POST_NOT_FOUND)
        );

        PostReactionId postReactionId = new PostReactionId(postId, userId);
        boolean existing = postScrapRepository.existsById(postReactionId);

        if (cmd && !existing) {
            try {
                postScrapRepository.save(PostScrap.builder().id(postReactionId).build());
                userStatsService.incScrappedPost(userId);
                existing = true;
            } catch (DataIntegrityViolationException e) {
                // 동시성으로 이미 들어간 경우 -> 멱등 처리
                existing = true;
            }
        } else if (!cmd && existing) {
            postScrapRepository.deleteById(postReactionId);
            userStatsService.decScrappedPost(userId);
            existing = false;
        }

        // 스크랩 개수 조회
        int scrapCount = postScrapRepository.countByPostId(postReactionId.postId());

        return new PostScrapResponse(existing, scrapCount);
    }

}
