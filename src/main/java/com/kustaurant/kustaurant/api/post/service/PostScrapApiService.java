package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.post.service.port.PostRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
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
    private final OUserRepository OUserRepository;
    public int scrapCreateOrDelete(PostEntity postEntity, UserEntity UserEntity) {
        List<PostScrapEntity> postScrapList = postEntity.getPostScrapList();
        List<PostScrapEntity> userScrapList = UserEntity.getScrapList();
        Optional<PostScrapEntity> scrapOptional = postScrapApiRepository.findByUserAndPost(UserEntity, postEntity);
        int status;

        if (scrapOptional.isPresent()) {
            PostScrapEntity scrap = scrapOptional.get();
            postScrapApiRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status = 0; // scrapDeleted
        } else {
            PostScrapEntity scrap = PostScrapEntity.builder()
                    .user(UserEntity)
                    .post(postEntity)
                    .createdAt(LocalDateTime.now())
                    .build();
            PostScrapEntity savedScrap = postScrapApiRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status = 1; // scrapCreated
        }

        postRepository.save(postEntity);
        OUserRepository.save(UserEntity);
        return status;
    }


}
