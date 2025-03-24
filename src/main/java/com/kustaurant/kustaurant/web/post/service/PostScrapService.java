package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapService {
    private final PostScrapRepository postScrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public Map<String, Object> scrapCreateOfDelete(PostEntity postEntity, User user){
        List<PostScrap> postScrapList = postEntity.getPostScrapList();
        List<PostScrap> userScrapList =user.getScrapList();
        Optional<PostScrap> scrapOptional = postScrapRepository.findByUserAndPostEntity(user, postEntity);
        Map<String, Object> status = new HashMap<>();
        if(scrapOptional.isPresent()){
            PostScrap scrap = scrapOptional.get();
            postScrapRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status.put("scrapDelete",true);
        }
        else{
            PostScrap scrap = new PostScrap(user, postEntity, LocalDateTime.now());
            PostScrap savedScrap= postScrapRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status.put("scrapCreated",true);

        }
        postRepository.save(postEntity);
        userRepository.save(user);
        return status;
    }

}
