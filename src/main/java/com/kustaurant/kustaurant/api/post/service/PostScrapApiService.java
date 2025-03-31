package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
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
        List<PostScrap> postScrapList = postEntity.getPostScrapList();
        List<PostScrap> userScrapList = UserEntity.getScrapList();
        Optional<PostScrap> scrapOptional = postScrapApiRepository.findByUserAndPostEntity(UserEntity, postEntity);
        int status;

        if (scrapOptional.isPresent()) {
            PostScrap scrap = scrapOptional.get();
            postScrapApiRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status = 0; // scrapDeleted
        } else {
            PostScrap scrap = new PostScrap(UserEntity, postEntity, LocalDateTime.now());
            PostScrap savedScrap = postScrapApiRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status = 1; // scrapCreated
        }

        postRepository.save(postEntity);
        OUserRepository.save(UserEntity);
        return status;
    }


}
