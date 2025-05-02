package com.kustaurant.kustaurant.web.post.service;

import com.kustaurant.kustaurant.common.post.infrastructure.*;
import com.kustaurant.kustaurant.common.post.infrastructure.PostEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
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
    private final OPostScrapRepository postScrapRepository;
    private final PostRepository postRepository;
    private final OUserRepository userRepository;
    public Map<String, Object> scrapCreateOfDelete(PostEntity postEntity, UserEntity user){
        List<PostScrapEntity> postScrapList = postEntity.getPostScrapList();
        List<PostScrapEntity> userScrapList =user.getScrapList();
        Optional<PostScrapEntity> scrapOptional = postScrapRepository.findByPostAndUser(postEntity, user);
        Map<String, Object> status = new HashMap<>();
        if(scrapOptional.isPresent()){
            PostScrapEntity scrap = scrapOptional.get();
            postScrapRepository.delete(scrap);
            postScrapList.remove(scrap);
            userScrapList.remove(scrap);
            status.put("scrapDelete",true);
        }
        else{
            PostScrapEntity scrap = new PostScrapEntity(user, postEntity, LocalDateTime.now());
            PostScrapEntity savedScrap= postScrapRepository.save(scrap);
            userScrapList.add(savedScrap);
            postScrapList.add(savedScrap);
            status.put("scrapCreated",true);

        }
        postRepository.save(postEntity);
        userRepository.save(user);
        return status;
    }

}
