package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.controller.response.ScrapToggleResponse;
import com.kustaurant.kustaurant.post.post.domain.PostReactionId;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.domain.enums.ScrapStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.user.mypage.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    private final UserStatsService userStatsService;

    public ScrapToggleResponse toggleScrapWithCount(Long postId, Long userId) {
        postRepository.findById(postId).orElseThrow(
                ()-> new DataNotFoundException(ErrorCode.POST_NOT_FOUND)
        );

        PostReactionId postReactionId = new PostReactionId(postId, userId);
        Optional<PostScrap> existing = postScrapRepository.findById(postReactionId);
        boolean isCreated;

        if (existing.isPresent()) {
            postScrapRepository.delete(existing.get());
            userStatsService.decScrappedPost(userId);
            isCreated = false;
        } else {
            try {
                postScrapRepository.save(
                        PostScrap.builder()
                                .id(postReactionId)
                                .build()
                );
                isCreated = true;
                userStatsService.incScrappedPost(userId);
            } catch (DataIntegrityViolationException e) {
                isCreated = true;
            }
        }

        // 스크랩 개수 조회
        int scrapCount = postScrapRepository.countByPostId(postReactionId.postId());
        ScrapStatus status = isCreated ? ScrapStatus.SCRAPPED : ScrapStatus.NOT_SCRAPPED;

        return new ScrapToggleResponse(scrapCount, status);
    }

}
