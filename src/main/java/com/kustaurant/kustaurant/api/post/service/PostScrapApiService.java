package com.kustaurant.kustaurant.api.post.service;


import com.kustaurant.kustaurant.common.post.infrastructure.Post;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrap;
import com.kustaurant.kustaurant.common.post.infrastructure.PostApiRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.PostScrapApiRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapApiService {
    private final PostScrapApiRepository postScrapApiRepository;
    private final PostApiRepository postApiRepository;
    private final UserRepository userRepository;
    public int scrapCreateOrDelete(Post post, User user) {
        List<PostScrap> postScrapList = post.getPostScrapList();
        List<PostScrap> userScrapList = user.getScrapList();
        Optional<PostScrap> scrapOptional = postScrapApiRepository.findByUserAndPost(user, post);
        int status;

        if (scrapOptional.isPresent()) {
            PostScrap scrap = scrapOptional.get();
            postScrapApiRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status = 0; // scrapDeleted
        } else {
            PostScrap scrap = new PostScrap(user, post, LocalDateTime.now());
            PostScrap savedScrap = postScrapApiRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status = 1; // scrapCreated
        }

        postApiRepository.save(post);
        userRepository.save(user);
        return status;
    }


}
