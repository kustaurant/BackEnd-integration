package com.kustaurant.kustaurant.post.post.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.controller.response.ScrapToggleResponse;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.domain.enums.ScrapStatus;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
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

    public ScrapToggleResponse toggleScrapWithCount(Long postId, Long userId) {
        postRepository.findById(postId).orElseThrow(
                ()-> new DataNotFoundException(ErrorCode.POST_NOT_FOUND)
        );

        Optional<PostScrap> existing = postScrapRepository.findByUserIdAndPostId(userId, postId);
        boolean isCreated;

        if (existing.isPresent()) {
            postScrapRepository.delete(existing.get());
            isCreated = false;
        } else {
            try {
                postScrapRepository.save(
                        PostScrap.builder()
                                .postId(postId)
                                .userId(userId)
                                .createdAt(LocalDateTime.now())
                                .build()
                );
                isCreated = true;
            } catch (DataIntegrityViolationException e) {
                isCreated = true;
            }
        }

        // 스크랩 개수 조회
        int scrapCount = postScrapRepository.countByPostId(postId);
        ScrapStatus status = isCreated ? ScrapStatus.SCRAPPED : ScrapStatus.NOT_SCRAPPED;

        return new ScrapToggleResponse(scrapCount, status);
    }

}
