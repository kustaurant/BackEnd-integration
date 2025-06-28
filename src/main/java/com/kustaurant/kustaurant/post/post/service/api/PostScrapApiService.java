package com.kustaurant.kustaurant.post.post.service.api;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.post.post.domain.PostScrap;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapApiService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;

    @Transactional
    public boolean toggleScrap(Integer postId, Long userId) {
        postRepository.findById(postId).orElseThrow(
                ()-> new DataNotFoundException(ErrorCode.POST_NOT_FOUNT)
        );

        Optional<PostScrap> existing = postScrapRepository.findByUserIdAndPostId(userId, postId);

        if (existing.isPresent()) {
            postScrapRepository.delete(existing.get());
            return false;
        }

        try {
            postScrapRepository.save(
                    PostScrap.builder()
                            .postId(postId)
                            .userId(userId)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            return true;
        } catch (DataIntegrityViolationException e) {
            return true;
        }
    }

}
