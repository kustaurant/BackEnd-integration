package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.post.infrastructure.PostScrapApiRepository;
import com.kustaurant.kustaurant.post.infrastructure.PostScrapEntity;
import com.kustaurant.kustaurant.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapApiService {
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostRepository postRepository;

    public int scrapCreateOrDelete(PostEntity postEntity, Long userId) {
        List<PostScrapEntity> postScrapList = postEntity.getPostScrapList();
        List<PostScrapEntity> userScrapList = UserEntity.getScrapList();
        Optional<PostScrapEntity> scrapOptional = postScrapApiRepository.findByUserAndPost(userId, postEntity);
        int status;

        if (scrapOptional.isPresent()) {
            PostScrapEntity scrap = scrapOptional.get();
            postScrapApiRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status = 0; // scrapDeleted
        } else {
            PostScrapEntity scrap = PostScrapEntity.builder()
                    .userId(userId)
                    .post(postEntity)
                    .createdAt(LocalDateTime.now())
                    .build();
            PostScrapEntity savedScrap = postScrapApiRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status = 1; // scrapCreated
        }

        postRepository.save(postEntity);
        return status;
    }


}
