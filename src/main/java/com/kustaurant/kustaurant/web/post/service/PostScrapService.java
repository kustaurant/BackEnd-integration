package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.service.port.PostScrapRepository;
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

    public Map<String, Object> toggleScrap(Long userId, Integer postId) {

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
