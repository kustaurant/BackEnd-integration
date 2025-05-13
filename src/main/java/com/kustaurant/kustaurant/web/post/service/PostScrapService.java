package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.post.domain.PostScrap;
import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.service.port.PostRepository;
import com.kustaurant.kustaurant.common.post.service.port.PostScrapRepository;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;

    public Map<String, Object> toggleScrap(Integer userId, Integer postId) {

        Optional<PostScrap> scrapOptional = postScrapRepository.findByUserIdAndPostId(userId, postId);
        Map<String, Object> status = new HashMap<>();
        if (scrapOptional.isPresent()) {
            PostScrap scrap = scrapOptional.get();
            postScrapRepository.delete(scrap);

            status.put("scrapDelete", true);
        } else {
            PostScrap scrap = PostScrap.create(userId, postId);
            postScrapRepository.save(scrap);
            status.put("scrapCreated", true);

        }

        return status;
    }

}
