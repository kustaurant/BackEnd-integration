package com.kustaurant.kustaurant.post.post.service.web;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    public Map<String, Object> toggleScrap(Long userId, Integer postId) {
        // 게시글 존재 확인
        postRepository.findById(postId).orElseThrow(
                () -> new DataNotFoundException(ErrorCode.POST_NOT_FOUND)
        );

        Optional<PostScrap> scrapOptional = postScrapRepository.findByUserIdAndPostId(userId, postId);
        Map<String, Object> status = new HashMap<>();
        if (scrapOptional.isPresent()) {
            PostScrap scrap = scrapOptional.get();
            postScrapRepository.delete(scrap);

            status.put("scrapDelete", true);
        } else {
            PostScrap scrap = PostScrap.builder().
                    userId(userId)
                    .postId(postId)
                    .createdAt(LocalDateTime.now())
                    .build();
            postScrapRepository.save(scrap);
            status.put("scrapCreated", true);
        }
        return status;
    }

}
